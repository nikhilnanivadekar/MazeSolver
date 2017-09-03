package robotics.maze;

import lejos.remote.ev3.RemoteRequestEV3;
import lejos.remote.ev3.RemoteRequestPilot;

import java.io.IOException;

public class DifferentialMotor
{
    private static final int TRAVERSAL_DISTANCE = 20;

    private final RemoteRequestPilot pilot;
    private final RemoteRequestEV3 ev3;

    public DifferentialMotor(
            double wheelDiameter,
            double trackWidth,
            String leftMotorPort,
            String rightMotorPort,
            double rotateSpeed,
            double travelSpeed,
            String ip) throws IOException
    {
        ev3 = new RemoteRequestEV3(ip);
        ev3.getPort(leftMotorPort);
        ev3.getPort(rightMotorPort);

        pilot = (RemoteRequestPilot) ev3.createPilot(wheelDiameter, trackWidth, leftMotorPort, rightMotorPort);
        pilot.setRotateSpeed(rotateSpeed);
        pilot.setTravelSpeed(travelSpeed);
    }

    public RemoteRequestPilot getPilot()
    {
        return this.pilot;
    }

    public RemoteRequestEV3 getEv3()
    {
        return this.ev3;
    }

    public void move(int distance)
    {
        this.pilot.travel(distance * TRAVERSAL_DISTANCE);
    }

    public void rotate(int angle)
    {
        this.pilot.rotate(angle);
    }
}
