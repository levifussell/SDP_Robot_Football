import pygame
from pygame.locals import *
from pygame.color import *
import pymunk
import pymunk.pygame_util
from pymunk import Vec2d
import math, sys, random


from State import State

REWARD_DIST = 300000
REWARD_TIME = -0.0001
REWARD_HIT_BALL = 10
REWARD_FAILED = -10

pygame.init()
screenSize = 600
screen = pygame.display.set_mode((screenSize, screenSize))
clock = pygame.time.Clock()
running = True

rewardTimerTicks = 0

# PHYSICS
space = pymunk.Space()
space.gravity = (0.0, 0.0)
draw_options = pymunk.pygame_util.DrawOptions(screen)

# BALLS
balls_bodies = []
balls_shapes = []

# ROBOTS
robots_bodies = []
robots_shapes = []

# WALLS
wallEdge = 10

def addWalls():
    static_body = space.static_body
    static_lines = [pymunk.Segment(static_body, (wallEdge, wallEdge), (600.0 - wallEdge, wallEdge), 0.0)
                    , pymunk.Segment(static_body, (wallEdge, wallEdge), (wallEdge, 600.0 - wallEdge), 0.0)
                    , pymunk.Segment(static_body, (wallEdge, 600 - wallEdge), (600 - wallEdge, 600 - wallEdge), 0.0)
                    , pymunk.Segment(static_body, (600 - wallEdge, 600 - wallEdge), (600 - wallEdge, wallEdge), 0.0)
                    ]

    for line in static_lines:
        line.elasticity = 0.95
        line.friction = 0.9
    space.add(static_lines)

def addRandomBall():
    mass = 30
    radius = 5
    inertia = pymunk.moment_for_circle(mass, 0, radius, (0, 0))
    body = pymunk.Body(mass, inertia)
    # x = random.randint(115, 350)
    body.position = 300, 300
    shape = pymunk.Circle(body, radius, (0, 0))
    shape.elasticity = 0.95
    shape.friction = 0.9
    space.add(body, shape)
    balls_shapes.append(shape)
    balls_bodies.append(body)

def addRandomRobot():
    mass = 10
    radius = 18
    inertia = pymunk.moment_for_circle(mass, 0, radius, (0, 0))
    body = pymunk.Body(mass, inertia)
    body.position = (wallEdge * 10, wallEdge * 10)
    shape = pymunk.Circle(body, radius, (0, 0))
    shape.elasticity = 0.1
    shape.friction = 0.9
    space.add(body, shape)
    robots_shapes.append(shape)
    robots_bodies.append(body)

def resetRobot():
    balls_bodies[0].position = (300, 300)
    balls_bodies[0].velocity = Vec2d(0, 0)
    robots_bodies[0].velocity = Vec2d(0, 0)
    robots_bodies[0].position = (random.randint(wallEdge, 600 - wallEdge), random.randint(wallEdge, 600 - wallEdge))
    # robots_bodies[0].position = (wallEdge * 30, wallEdge * 10)

def setTestPos():
    robots_bodies[0].velocity = Vec2d(0, 0)
    robots_bodies[0].position = (wallEdge * 10, wallEdge * 10)
    balls_bodies[0].position = (500, 500)
    balls_bodies[0].velocity = Vec2d(0, 0)


def moveRobot(x, y):
    robots_bodies[0].velocity = 100 * Vec2d(x, y)#.rotated(robots_bodies[0].angle)
def moveRobotLeft(): moveRobot(-1, 0)
def moveRobotRight(): moveRobot(1, 0)
def moveRobotForward(): moveRobot(0, 1)
def moveRobotBackward(): moveRobot(0, -1)

def getCurrentStateVector():
    # return the new state
    (robotX, robotY) = robots_bodies[0].position
    veloc = robots_bodies[0].velocity
    velocX = veloc.x
    velocY = veloc.y
    (ballX, ballY) = balls_bodies[0].position

    stateVec = (robotX, robotY, velocX, velocY, ballX, ballY)
    return stateVec

def selectBestActionProb(actionVector, probChoosingRandom):
    prob = random.randfloat(0, 1)

    if prob > probChoosingRandom:
        randAction = random.randint(0, len(actionVector) - 1)
        return randAction
    else:
        return actionVector.index(max(actionVector))

def makeMove(action):
    # move left
    if action == 0:
        # print("MOVE LEFT")
        moveRobotLeft()

    # move right
    elif action == 1:
        # print("MOVE RIGHT")
        moveRobotRight()

    # move forward
    elif action == 2:
        # print("MOVE FORWARD")
        moveRobotForward()

    # move backward
    elif action == 3:
        # print("MOVE BACK")
        moveRobotBackward()


def getReward(state):
    # reward is inversely proportional to the distance to the ball
    distX = state.getRobotX() - state.getBallX()
    distY = state.getRobotY() - state.getBallY()
    distSqr = distX * distX + distY * distY
    distReward = REWARD_DIST / distSqr

    # if the robot is close enough to collide with the ball, give the maximum positive reward to end that game (epoch)
    if distSqr < 40*40:
        return REWARD_HIT_BALL
    if rewardTimerTicks > 100:
        # print("FINAL DIST:{}".format(distSqr))
        return REWARD_FAILED
    else:
        # total reward is the reward for being close to the ball minus the time it has taken to get to the ball
        return distReward + REWARD_TIME * rewardTimerTicks

def getReward2(state):
    # we want to check that the velocity we are going at is towards the ball
    distX = state.getRobotX() - state.getBallX()
    distY = state.getRobotY() - state.getBallY()
    distSqr = distX * distX + distY * distY
    distReward = REWARD_DIST / distSqr

    reward = -distSqr / REWARD_DIST * 100
    if state.getRobotX() < state.getBallX() and state.getVelocityX() > 0:
        reward = distReward
    elif state.getRobotX() > state.getBallX() and state.getVelocityX() < 0:
        reward = distReward

    if state.getRobotY() < state.getBallY() and state.getVelocityY() > 0:
        reward = distReward
    elif state.getRobotX() > state.getBallY() and state.getVelocityY() < 0:
        reward = distReward

    if distSqr < 40 * 40:
        return REWARD_HIT_BALL

    if rewardTimerTicks > 100:
        return REWARD_FAILED

    # check that the robot does not go near the walls. Without this,
    # it learns that it can run into the wall to maximise its velocity and
    # score positive reward every time
    if state.getRobotX() < wallEdge * 2 or state.getRobotX() > screenSize - wallEdge * 2:
        return REWARD_FAILED
    if state.getRobotY() < wallEdge * 2 or state.getRobotY() > screenSize - wallEdge * 2:
        return REWARD_FAILED

    return reward

# BASIC STARTING SETUP

def createWorld():
    addWalls()
    addRandomBall()
    addRandomRobot()
    # moveRobotLeft()

# add 1 random balls
# inumBalls = 1
# for i in range(inumBalls):
    # addRandomBall()

# # add a random player
# addRandomRobot()
# moveRobotLeft()

def runGamePhysics():
    # UPDATE PHYSICS
    dt = 1.0/30.0 #1/60
    for x in range(1):
        space.step(dt)

def makeMoveAndUpdatePhysics(action):
    makeMove(action)
    runGamePhysics()

def makeMoveAndUpdatePhysicsAndDraw(action):
    makeMove(action)
    runAndDrawOneGameFrame()

def runAndDrawOneGameFrame():

    for event in pygame.event.get():
        if event.type == QUIT:
            return True
        elif event.type == KEYDOWN and event.type == K_ESCAPE:
            return True
        elif event.type == KEYDOWN and event.key == K_p:
            pygame.image.save(screen, "bounding_balls.png")

    # CLEAR SCREEN
    screen.fill(THECOLORS["white"])

    # DRAW STUFF
    space.debug_draw(draw_options)

    runGamePhysics()

    # FLIP SCREEN
    pygame.display.flip()
    clock.tick(50)
    currentRewardVal = getReward2(State(getCurrentStateVector()))
    pygame.display.set_caption("reward:{}".format(currentRewardVal)  + "| state:{}".format(getCurrentStateVector()[3]))

    return False


# while running:
    # for event in pygame.event.get():
        # if event.type == QUIT:
            # running = False
        # elif event.type == KEYDOWN and event.type == K_ESCAPE:
            # running = False
        # elif event.type == KEYDOWN and event.key == K_p:
            # pygame.image.save(screen, "bounding_balls.png")

    # # update reward penalty for taking too long to find the ball
    # rewardTimerTicks += 1

    # runAndDrawOneGameFrame()
