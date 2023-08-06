import java.awt.*;

public abstract class TemplateObject implements Drawable, Updatable, Pressable, Resettable
{
    double x, y, vx, vy;
    Color cl;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public Color getCl() {
        return cl;
    }

    public void setCl(Color cl) {
        this.cl = cl;
    }
}
