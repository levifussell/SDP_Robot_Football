import pygame
from pygame.locals import *
from pygame.color import *
import pymunk
import pymunk.pygame_util
from pymunk import Vec2d
import math, sys, random

from State import State

REWARD_DIST = 3000000
REWARD_TIME = -0.1
REWARD_HIT_BALL = 10000

pygame.init()
screen = pygame.display.set_mode((600, 600))
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
    mass = 10
    radius = 5
    inertia = pymunk.moment_for_circle(mass, 0, radius, (0, 0))
    body = pymunk.Body(mass, inertia)
    # x = random.randint(115, 350)
    body.position = 100, 400
    shape = pymunk.Circle(body, radius, (0, 0))
    shape.elasticity = 0.95
    shape.friction = 0.9
    space.add(body, shape)
    balls_shapes.append(shape)
    balls_bodies.append(body)

def addRandomRobot():
    mass = 30
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

def moveRobot(x, y):
    robots_bodies[0].velocity = 100 * Vec2d(x, y).rotated(robots_bodies[0].angle)
def moveRobotLeft(): moveRobot(0, 1)
def moveRobotRight(): moveRobot(0, -1)
def moveRobotForward(): moveRobot(1, 0)
def moveRobotBackward(): moveRobot(-1, 0)

def getCurrentStateVector():
    # return the new state
    (robotX, robotY) = robots_bodies[0].position
    (ballX, ballY) = balls_bodies[0].position

    stateVec = (robotX, robotY, ballX, ballY)
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
        print("MOVE LEFT")
        moveRobotLeft()

    # move right
    elif action == 1:
        print("MOVE RIGHT")
        moveRobotRight()

    # move forward
    elif action == 2:
        print("MOVE FORWARD")
        moveRobotForward()

    # move backward
    elif action == 3:
        print("MOVE BACK")
        moveRobotBackward()


def getReward(state):
    # reward is inversely proportional to the distance to the ball
    distX = state.getRobotX() - state.getBallX()
    distY = state.getRobotY() - state.getBallY()
    distSqr = distX * distX + distY * distY
    distReward = REWARD_DIST / distSqr

    # if the robot is close enough to collide with the ball, give the maximum positive reward to end that game (epoch)
    if distSqr < 40*40:
        return REWARD_HIT_BALL;
    else:
        # total reward is the reward for being close to the ball minus the time it has taken to get to the ball
        return distReward + REWARD_TIME * rewardTimerTicks

# BASIC STARTING SETUP

def createWorld():
    addWalls()
    addRandomBall()
    addRandomRobot()
    moveRobotLeft()


# add 1 random balls
# inumBalls = 1
# for i in range(inumBalls):
    # addRandomBall()

# # add a random player
# addRandomRobot()
# moveRobotLeft()


def runAndDrawOneGameFrame():

    # CLEAR SCREEN
    screen.fill(THECOLORS["white"])

    # DRAW STUFF
    space.debug_draw(draw_options)

    # UPDATE PHYSICS
    dt = 1.0/60.0
    for x in range(1):
        space.step(dt)

    # FLIP SCREEN
    pygame.display.flip()
    clock.tick(50)
    currentRewardVal = getReward(State(getCurrentStateVector()))
    pygame.display.set_caption("state:{}".format(currentRewardVal)  + "| time_neg_reward:{}".format(rewardTimerTicks))


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
