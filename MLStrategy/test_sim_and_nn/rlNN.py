from __future__ import print_function

#import MNIST
from tensorflow.examples.tutorials.mnist import input_data
mnist = input_data.read_data_sets("/tmp/data/", one_hot=True)
import tensorflow as tf

import numpy as np

# import the game file
import robotSim2.py as sim

learning_rate = 0.001
training_epochs = 7
batch_size = 100
display_step = 1
prob_of_choosing_random_action = 0.5

n_input = 784
n_hidden_1 = 100
n_output = 10

print(np.shape(mnist.test.labels))
print(np.shape(np.array([mnist.test.labels[0]])))

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

def getWeightMeans(w):
    weightData = []
    for i in range(len(w)):
        # print('-- ' + str(i) + ' --')
        # print(np.mean(w[i]), np.std(w[i]))
        weightData.append(np.mean(w[i]))

    return np.array(weightData)

#create the model
pred = createNN(x, weights, biases)

cost = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits=pred, labels=y))
optimiser = tf.train.AdamOptimizer(learning_rate=learning_rate).minimize(cost)

#initialise the variables
init = tf.global_variables_initializer()

#create a saver to save the model state (variables)
saver = tf.train.Saver()

def argRand(tensor):
    # randomly shuffle the tensor and choose the max value
    #  (this is equivalent to choosing a random value)
    tf.argmax(tf.random_shuffle(tensor))

#launch the graph
with tf.Session() as sess:
    sess.run(init)

    wmStart = getWeightMeans(sess.run(weights['h1']))

    #training cycle
    for epoch in range(training_epochs):
            # do one itteration of the game
            sim.runAndDrawOneGameFrame()

            # get the current state of the world
            s_t = np.array([sim.getCurrentStateVector()])

            # run the state vector through the NN
            #  choose the BEST action
            a_t_out = tf.argmax(pred, 1)
            #  choose a random action
            random_prediction = predict_out.eval({x: np.array([mnist.test.images[0]])})

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
        if epoch % display_step == 0:
            print("Epoch:", '%04d' % (epoch), "cost={:.9f}".format(avg_cost))

    print("Optimisation Finished!")

    #save the current model
    save_path = saver.save(sess, "/tmp/modelT.ckpt")
    print("Model saved at: %s" % save_path)

    sess.close()

#reload the old sess and test the model
with tf.Session() as sess:

    #restore the variables from disk
    saver.restore(sess, "/tmp/modelT.ckpt")
    print("Model restored")

    #Test Model
    correct_prediction = tf.equal(tf.argmax(pred, 1), tf.argmax(y, 1))
    #calculate accuracy
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, "float"))
    print("Accuracy:", accuracy.eval({x: mnist.test.images, y: mnist.test.labels}))

    #outcome of an individial input
    predict_out = tf.argmax(pred, 1)
    random_prediction = predict_out.eval({x: np.array([mnist.test.images[0]])})
    print(random_prediction)

    wmEnd = getWeightMeans(sess.run(weights['h1']))
    wmDiff = wmStart - wmEnd
    # for i in range(0, len(wmDiff)):
        # print(wmDiff[i])

