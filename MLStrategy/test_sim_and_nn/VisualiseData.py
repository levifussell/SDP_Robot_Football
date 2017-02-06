import numpy as np
import matplotlib.pyplot as plt

dataRewardOverTime = []
learningErrorOverTime = []

# nn outputs

def plotDataRewardOverTime():
    x = np.r_[0:len(dataRewardOverTime)]
    # r = np.array(dataRewardOverTime)
    # print(r)
    plt.figure(1)
    plt.title("REWARD OVER TIME")
    plt.plot(x, dataRewardOverTime)
    # plt.show()
    # print(min(dataRewardOverTime))

def plotLearningErrorOverTime():
    x = np.r_[0:len(learningErrorOverTime)]
    # r = np.array(learningErrorOverTime)
    # print(r)
    plt.figure(2)
    plt.title("COST ERROR OVER TIME (LEARNING)")
    plt.plot(x, learningErrorOverTime)
    # plt.show()
    # print(min(dataRewardOverTime))

def display():
    plotDataRewardOverTime()
    plotLearningErrorOverTime()
    plt.show()
