import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Player extends TemplateObject
{
    double x, y, radius;
    double vx, vy;
    public Color cl;
    double score;

    public Player(double x, double y, double radius)
    {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.vx = 2;
        this.vy = 5;
        cl = Color.WHITE;
        this.score = 0;
    }

    @Override
    public void draw()
    {
        StdDraw.setPenColor(cl);

        double vxNorm = Math.abs(vx / 3.5);
        double vyNorm = Math.abs(vy / (Game.maxVY / 2));

        if (vxNorm < 1) vxNorm = 1;
        if (vyNorm < 1) vyNorm = 1;

        StdDraw.filledEllipse(x, y, radius * vxNorm, radius * vyNorm);

        // Splat
//        StdDraw.filledEllipse(x, y, radius, radius / 2);
    }

    @Override
    public void update()
    {
        x = x + vx;
        y = y + vy;

        vy = Math.max(vy - Game.G, Game.minVY);

        if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) vx = Math.min(Game.maxVX, vx + Game.VX);
        if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) vx = Math.max(-Game.maxVX, vx - Game.VX);
    }

    public void applyHit()
    {
        vy = -vy;
        if (vy < Game.hitVY)
            vy = Game.hitVY;
        if (vy > Game.maxVY)
            vy = Game.maxVY;
    }

    @Override
    public void onPress() {

    }

    @Override
    public void reset()
    {
        //
        score = 0;
    }
}
