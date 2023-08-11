import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Player extends TemplateObject
{
    double x, y, radius;
    double vx, vy;
    public Color cl;

    public Player(double x, double y, double radius)
    {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.vx = 2;
        this.vy = 5;
        cl = Color.WHITE;
    }

    @Override
    public void draw()
    {
        StdDraw.setPenColor(cl);

        /*
            Draw a deformed circle based on current velocity
         */
        double vxNorm = Math.abs(vx / 3.5);
        double vyNorm = Math.abs(vy / (Game.maxVY / 2));

        if (vxNorm < 1) vxNorm = 1;
        if (vyNorm < 1) vyNorm = 1;

        StdDraw.filledEllipse(x, y, radius * vxNorm, radius * vyNorm);
    }

    @Override
    public void update()
    {
        x = x + vx;
        y = y + vy;

        vy = Math.max(vy - Game.G, Game.minVY);

        /*
            Changing direction has more effect on velocity up to a certain limit.
         */
        double strength = Game.VX;
        if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT))
        {
            if (vx < 0) strength = strength * 2; // Pressing right but current direction is left
            vx = Math.min(Game.maxVX, vx + strength);
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT))
        {
            if (vx > 0) strength = strength * 2; // Pressing left but current direction is right
            vx = Math.max(-Game.maxVX, vx - strength);
        }
    }

    @Override
    public void reset()
    {
        //
    }

    public void collide(TemplateObject to)
    {
        if (to.getClass() == Obstacle.class) collide( (Obstacle) to);
    }

    public void collide(Obstacle o)
    {
        /*
            Hit obstacle, flip y velocity between a defined limit
         */
        vy = -vy;
        if (vy < Game.hitVY)
            vy = Game.hitVY;
        if (vy > Game.maxVY)
            vy = Game.maxVY;
    }
}
