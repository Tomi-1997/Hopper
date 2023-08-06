import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Game
{
    private final Collection<TemplateObject> TO;
    private Player p;
    private double lastCollision = 0;
    private boolean restartAvailable = true;

    private static final long FPS = 1000 / 60;
    public static final int obstacles = 10, maxX = 800, maxY = 400, obstacleEPS = 20, pEPS = 5, hitReward = 10;
    public static double G = 0.15;
    public static final double minVY = -10;
    public static final double maxVY = 7;
    public static final double maxVX = 4;
    public static final double VX = 0.2;
    public static final double hitVY = 5;

    public Game()
    {
        TO = Collections.synchronizedCollection(new ArrayList<>());
        createPlayer();
        createObstacles((int) (maxX * 0.25), maxY / 2);
        createInformation();
    }

    private void createInformation()
    {
        Information i = new Information(maxX, maxY, this.p);
        TO.add(i);
    }

    private void createObstacles(int x, int y)
    {
        for (int i = 0; i < obstacles; i++)
        {
            int w = randInt(50, 60);
            int h = randInt(5, 10);
            double speed = 1.5 + Math.random();
            Obstacle o = new Obstacle(x, y, w / 2.0, h / 2.0, speed);
            TO.add(o);

            x = x + randInt(150, 200);
            y = y + randInt(-30, 30);
        }
    }

    private void createPlayer()
    {
        Player p = new Player(0, maxY / 2.0, 5);
        TO.add(p);
        this.p = p;
    }

    public void run()
    {
        StdDraw.setCanvasSize(maxX, maxY);
        StdDraw.setXscale(0, maxX);
        StdDraw.setYscale(0, maxY);
        StdDraw.setPenRadius(0.004);

        while ( isRunning() )
        {
            StdDraw.clear(StdDraw.BLACK);
            iterate();
            StdDraw.show(0);
            delay();
        }
        System.exit(0);
    }

    private void iterate()
    {
        synchronized (TO) { for (Updatable u : TO) u.update(); }
        synchronized (TO) { for (Drawable  d : TO) d.draw(); }
        checkCollision();
    }

    private void restart()
    {
        restartAvailable = false;
        /*
            Print text to user indicating a reset is near
         */
        int duration = 1000;
        int dots = 3;
        StringBuilder text = new StringBuilder("Restarting");
        for (int i = 0; i <= dots; i++)
        {
            final String lambdaText = text.toString();
            TemplateObject d = new TemplateObject()
            {
                @Override
                public void update() {}
                @Override
                public void reset() {}
                @Override
                public void onPress() {}
                @Override
                public void draw() { StdDraw.textLeft(maxX / 2.3, maxY / 1.5, lambdaText); }
            };

            add(d);
            delay(duration / dots);
            rm(d);
            TO.remove(d);
            text.append(".");
        }

        /*
            Perform reset() for each object
         */

        for (TemplateObject to : TO)
            to.reset();

        int dropDuration = maxY / 2;
        createObstacles((int) (maxX * 0.4), (int) (maxY * 1.5));

        for (int i = 0; i < dropDuration; i++)
        {
            synchronized (TO)
            {
                for (TemplateObject u : TO)
                {
                    if (u.getClass() != Obstacle.class) continue;
                    Obstacle o = (Obstacle) u;
                    o.y = o.y - 2;
                }
            }
            delay(10);
        }


        delay(1000);

        TO.removeIf(TemplateObject::isReset);
        restartAvailable = true;
    }

    private boolean isRunning()
    {
        //
        if (restartAvailable && StdDraw.isKeyPressed(KeyEvent.VK_R)) { new Thread(this::restart).start(); }
        return !StdDraw.isKeyPressed(KeyEvent.VK_Q);
    }

    private void checkCollision()
    {

        Player p = null;
        ArrayList<Obstacle> arr = new ArrayList<>();

        /*
            Find player and obstacles from updatable \ drawables
         */
        synchronized (TO)
        {
            for (TemplateObject u : TO)
            {
            if (u.getClass() == Player.class)
                p = (Player) u;
            if (u.getClass() == Obstacle.class)
                arr.add((Obstacle) u);
            }
        }

        if (p == null) return;

        /*
            If the player drops below screen - reset score and launch him back
         */
        if (p.y < -maxY * 0.2)
        {
            p.vy = maxY / 40.0 + 2;
            p.score = 0;
        }

        /*
            If the player is above the screen- draw an indicator line
         */
        if (p.y > maxY * 1.05)
        {
            StdDraw.setPenColor(p.cl);
            StdDraw.line(p.x, maxY * 1.1, p.x, maxY * 0.9);
        }

        /*
            If the player hits a wall, bounce him back at max X speed and give a little upwards boost
         */
        if (p.x + p.radius < -maxX * 0.05) {p.vx = maxVX; p.vy = Math.min(p.vy + 2, Game.maxVY);}
        if (p.x - p.radius> maxX * 1.05) {p.vx = -maxVX; p.vy = Math.min(p.vy + 2, Game.maxVY);}
        if (System.currentTimeMillis() - lastCollision < 500) return;

        /*
            Check for each obstacle if the player hit them
         */
        for (Obstacle o : arr)
        {
            if (p.x + p.radius + pEPS > o.x - o.halfWidth && p.x - p.radius + pEPS < o.x + o.halfWidth &&
                    p.y + p.radius - pEPS > o.y - o.halfHeight && p.y - p.radius - pEPS < o.y + o.halfHeight)
            {
                p.applyHit();
                o.applyHit();
                lastCollision = System.currentTimeMillis();
                Color temp = o.cl;
                o.cl = p.cl;
                p.cl = temp;
                p.score += hitReward;

                generateDust(o);
                return;
            }
        }
    }

    public static void delay()
    {
        //
        try {Thread.sleep(FPS);} catch (InterruptedException e) {e.printStackTrace();}
    }

    public static void delay(long millis)
    {
        //
        try {Thread.sleep(millis);} catch (InterruptedException e) {e.printStackTrace();}
    }

    private void generateDust(Obstacle o)
    {
        new Thread(() -> generateDust_(o)).start();
    }


    private void generateDust_(Obstacle o)
    {
        int fireworksNum = 10;
        double radius = 5;
        ArrayList<TemplateObject> obstacleHitDust = new ArrayList<>();
        for (int i = 0; i < fireworksNum; i++)
        {
            obstacleHitDust.add(new Firework(o.x, o.y, radius, false, o.cl));
        }

        addAll(obstacleHitDust);
        delay(2 * 1000);
        rmAll(obstacleHitDust);

    }

    private void rm(TemplateObject d)
    {
        synchronized (TO) {
            TO.remove(d);
        }
    }

    private void add(TemplateObject d)
    {
        synchronized (TO) {
            TO.add(d);
        }
    }

    private void rmAll(ArrayList<TemplateObject> obstacleHitDust)
    {
        //
        synchronized (TO) {TO.removeAll(obstacleHitDust); }
    }

    private void addAll(ArrayList<TemplateObject> obstacleHitDust)
    {
        //
        synchronized (TO) {TO.addAll(obstacleHitDust); }
    }

    private int randInt(int a, int b)
    {
        //
        return (int) (Math.random() * (b - a)) + a;
    }

}
