import numpy as np
import matplotlib.pyplot as plt

dataRewardOverTime = []

# nn outputs

def plotDataRewardOverTime():
    x = np.r_[0:len(dataRewardOverTime)]
    r = np.array(dataRewardOverTime)
    print(r)
    plt.plot(x, dataRewardOverTime)
    plt.show()
    print(min(dataRewardOverTime))

def display():
    plotDataRewardOverTime()
