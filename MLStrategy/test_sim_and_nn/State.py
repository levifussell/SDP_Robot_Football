
class State:

    def __init__(self, stateVector):
        self.vector = stateVector
        # (robotX, robotY, velocX, velocY, ballX, ballY)

    def setVector(self, newVector):
        self.vector = newVector
    def getVector(self): return self.vector

    def getRobotX(self): return self.vector[0]
    def getRobotY(self): return self.vector[1]
    def getVelocityX(self): return self.vector[2]
    def getVelocityY(self): return self.vector[3]
    def getBallX(self): return self.vector[4]
    def getBallY(self): return self.vector[5]
