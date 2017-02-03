from __future__ import print_function

#import MNIST
# from tensorflow.examples.tutorials.mnist import input_data
# mnist = input_data.read_data_sets("/tmp/data/", one_hot=True)
import tensorflow as tf

import numpy as np

# import the game file
import robotSim2 as sim
import VisualiseData as vd
from State import State

learning_rate = 0.0001
training_epochs = 10
batch_size = 100
display_step = 1
prob_of_choosing_random_action = 1.0
rateOfprobDecresease = prob_of_choosing_random_action / training_epochs

n_input = 6
n_hidden_1 = 400
n_output = 4

# print(np.shape(mnist.test.labels))
# print(np.shape(np.array([mnist.test.labels[0]])))

#graph input
x = tf.placeholder("float", [None, n_input])
y = tf.placeholder("float", [None, n_output])

def createNN(x, weights, biases):
    #hidden layer 1 - ReLU activation from input to hidden
    layer_1 = tf.add(tf.matmul(x, weights['h1']), biases['h1'])
    layer_1 = tf.nn.relu(layer_1)

    #output layer - linear activation from hidden 1 to ouput
    layer_out = tf.matmul(layer_1, weights['out'] + biases['out'])

    return layer_out

weights = {
    'h1': tf.Variable(tf.random_normal([n_input, n_hidden_1])),
    'out': tf.Variable(tf.random_normal([n_hidden_1, n_output]))
}

biases = {
    'h1': tf.Variable(tf.random_normal([n_hidden_1])),
    'out': tf.Variable(tf.random_normal([n_output]))
}

# test function
def getWeightMeans(w):
    weightData = []
    for i in range(len(w)):
        # print('-- ' + str(i) + ' --')
        # print(np.mean(w[i]), np.std(w[i]))
        weightData.append(np.mean(w[i]))

    return np.array(weightData)

#create the model
pred = createNN(x, weights, biases)

# cost = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits=pred, labels=y))
cost = tf.reduce_mean(tf.squared_difference(pred, y))
optimiser = tf.train.RMSPropOptimizer(learning_rate=learning_rate, decay=0.9).minimize(cost)

#initialise the variables
init = tf.global_variables_initializer()

#create a saver to save the model state (variables)
saver = tf.train.Saver()


# def argRand(tensor):
    # randomly shuffle the tensor and choose the max value
    #  (this is equivalent to choosing a random value)
    # tf.argmax(tf.random_shuffle(tensor))
    # randV = np.random()

#launch the graph
with tf.Session() as sess:
    sess.run(init)


    wmStart = getWeightMeans(sess.run(weights['h1']))
    print(wmStart)

    # Initialise the first state of the world
    sim.createWorld()

    rateOfDisplay = 1

    drawTrain = False
    debug = False

    #training cycle
    for epoch in range(training_epochs):

        gameFailed = False
        gameOverState = "NONE"
        sim.rewardTimerTicks = 0
        sim.resetRobot()

        # run the state vector through the NN
        #  choose the BEST action
        a_t_out = tf.argmax(pred, 1)

        print("prob of random action:{}".format(prob_of_choosing_random_action))

        while gameFailed == False:

            if debug:
                print("{}---------------------------------------".format(epoch))

            sim.rewardTimerTicks += 1

            # if epoch % rateOfDisplay == 0:
                # do one itteration of the game
            # sim.runAndDrawOneGameFrame()

            # get the current state of the world
            s_t = np.array([sim.getCurrentStateVector()])
            if debug:
                print("state1: {}".format(s_t[0]))


            a_1_out = sess.run(pred, feed_dict={x: s_t})[0]
            # if debug:
            # print("NN1 out1:{}".format(a_1_out))
            best_prediction = a_t_out.eval({x: s_t})
            if debug:
                print("NN-out best output: {}".format((best_prediction[0])))
            # print(sess.run(pred[tf.argmax(pred, 1)], feed_dict={x: s_t[0]}))

            #randomly either choose the best value or a random move
            randPoss = np.random.rand()
            if randPoss < prob_of_choosing_random_action:
                rand = np.random.randint(0, 3)
                if debug:
                    print("RANDOM ACTION CHOSEN: {}".format(rand))
                best_prediction[0] = rand

            # best_prediction[0] = 0

            prob_of_choosing_random_action -= .01/training_epochs#rateOfprobDecresease
            if debug:
                print("prob of choosing:{}".format(prob_of_choosing_random_action))

            # run the BEST action through the network
            # sim.makeMove(best_prediction[0])

            # sim.runAndDrawOneGameFrame()

            if drawTrain:
                sim.makeMoveAndUpdatePhysicsAndDraw(best_prediction[0])
            else:
                sim.makeMoveAndUpdatePhysics(best_prediction[0])

            # get the 2nd gen. state
            s_t_2 = np.array([sim.getCurrentStateVector()])

            # get the reward associated with this first action's state
            r_t = sim.getReward2(State(s_t_2[0]))
            vd.dataRewardOverTime.append(r_t)
            if debug:
                print("reward for our action:{}".format(r_t))

                print("state2: {}".format(s_t_2[0]))

            # run 2nd gen state through the network again
            #  and get the BEST
            best_prediction_2 = a_t_out.eval({x: s_t_2})
            a_2_out = sess.run(pred, feed_dict={x: s_t_2})[0]
            if debug:
                print("NN2 out1:{}".format(a_2_out))
            best_prediction_2 = a_t_out.eval({x: s_t_2})
            if debug:
                print("NN-out best output 2: {}".format((best_prediction_2[0])))

            # get the reward associated with this prediction
            # r_t_2 = sim.getReward(State(s_t_2[0]))

            target_a_t = r_t + 0.9 * a_2_out[best_prediction_2[0]]
            # if we FAILED the game because it took to long, take the worst reward
            if r_t == sim.REWARD_FAILED:
                gameOverState = "FAIL"
                target_a_t = r_t
                gameFailed = True # start new epoch after this train
            elif r_t == sim.REWARD_HIT_BALL:
                gameOverState = "WIN"
                target_a_t = r_t
                gameFailed = True # start new epoch after this train

            if debug:
                print("target val:{}".format(target_a_t))

            target_array = np.copy(a_1_out)
            target_array[best_prediction[0]] = target_a_t
            if debug:
                print("target vector:{}".format(target_array))
                print("actual vector:{}".format(a_1_out))

            # train the network
            _, c = sess.run([optimiser, cost], {x: s_t, y: [target_array]})

            # in each epoch we want to train one state of the game
            # (for now). We will not be using a batch implementation until later

            # avg_cost = 0.0
            # total_batch = int(mnist.train.num_examples/batch_size)

            # #loop over all batches
            # for i in range(total_batch):
                # batch_x, batch_y = mnist.train.next_batch(batch_size)
            # #run optimisation (backprop) and cost optimisation (to get loss value)
            # _, c = sess.run([optimiser, cost], feed_dict={x: batch_x, y: batch_y})

            # #compute the average loss
            # avg_cost += c/total_batch

            #display the logs per epoch step
            # if epoch % display_step == 0:
                # print("Epoch:", '%04d' % (epoch), "cost={:.9f}".format(avg_cost))

        print("EPOCH: {} - {}".format(epoch, gameOverState))

    print("Optimisation Finished!")

    runGameAfter = True

    if runGameAfter:
        # now run the final game
        runtime = 10000
        # sim.resetRobot()
        sim.setTestPos()
        while runtime > 0:
                # get the current state of the world
            s_t = np.array([sim.getCurrentStateVector()])
            if debug:
                print("state1: {}".format(s_t[0]))

            # run the state vector through the NN
            #  choose the BEST action
            a_t_out = tf.argmax(pred, 1)

            a_1_out = sess.run(pred, feed_dict={x: s_t})[0]
            # if debug:
            print("NN1 out1:{}".format(a_1_out))
            best_prediction = a_t_out.eval({x: s_t})
            print("NN-out best output: {}".format((best_prediction[0])))
            # print(sess.run(pred[tf.argmax(pred, 1)], feed_dict={x: s_t[0]}))

            print("STATE:{}".format(s_t))

            # run the BEST action through the network
            sim.makeMove(best_prediction[0])

            endGame = sim.runAndDrawOneGameFrame()

            runtime -= 1
            # endGame = sim.runAndDrawOneGameFrame()

            if endGame:
                break

        #save the current model
        save_path = saver.save(sess, "/tmp/modelT.ckpt")
        print("Model saved at: %s" % save_path)

        sess.close()


    # dispaly results
    vd.display()


#reload the old sess and test the model
# with tf.Session() as sess:

    # #restore the variables from disk
    # saver.restore(sess, "/tmp/modelT.ckpt")
    # print("Model restored")

    # #Test Model
    # correct_prediction = tf.equal(tf.argmax(pred, 1), tf.argmax(y, 1))
    # #calculate accuracy
    # accuracy = tf.reduce_mean(tf.cast(correct_prediction, "float"))
    # print("Accuracy:", accuracy.eval({x: mnist.test.images, y: mnist.test.labels}))

    # #outcome of an individial input
    # predict_out = tf.argmax(pred, 1)
    # random_prediction = predict_out.eval({x: np.array([mnist.test.images[0]])})
    # print(random_prediction)

    # wmEnd = getWeightMeans(sess.run(weights['h1']))
    # wmDiff = wmStart - wmEnd
    # # for i in range(0, len(wmDiff)):
        # # print(wmDiff[i])

