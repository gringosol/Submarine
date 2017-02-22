package com.gameinstance.submarine;

/**
 * Created by gringo on 29.12.2016 11:25.
 *
 */
public class Slider {
    Sprite line;
    Sprite circle;
    TouchHandler touchHandler;
    Layer layer;
    float left;
    float width;
    float value = 0.0f;

    public Slider(float x, float y, final float width, float height, int order) {
        this.width = width;
        left = x - width / 2.0f;
        int lineTexHandle = TextureManager.getTextureHandle(GameManager.getRenderer()
                .getActivityContext(), R.drawable.sliderline);
        int circleTexHandle = TextureManager.getTextureHandle(GameManager.getRenderer()
                .getActivityContext(), R.drawable.slidercircle);
        line = new Sprite(lineTexHandle, GameManager.getMovablePrimitiveMap(),
                new float[] {width, height}, new float[] {x, y});
        circle = new Sprite(circleTexHandle, GameManager.getMovablePrimitiveMap(),
                new float[] {height, height}, new float[] {left, y});
        touchHandler = new TouchHandler(order) {
            @Override
            public boolean touch(int x, int y) {
                float [] p = GameManager.getRenderer().convertCoords(x, y, true);
                if (inBounds(p[0], p[1])) {
                    circle.setPosition(p[0], circle.getPosition()[1]);
                    value = (p[0] - left) / width;
                    onValueChange(value);
                }
                return false;
            }

            @Override
            public boolean onDown(int x, int y) {
                return false;
            }

            @Override
            public boolean onUp(int x, int y) {
                return false;
            }
        };
        InputController.addTouchHandler(touchHandler);
    }

    public void addToLayer(Layer layer) {
        this.layer = layer;
        layer.addSprite(line);
        layer.addSprite(circle);
    }

    public void setValue(float value) {
        this.value = value;
        circle.setPosition(left + value * width,  circle.getPosition()[1]);
    }

    public Float getValue(){
        return value;
    }

    public void onValueChange(Float value) {

    }

    private boolean inBounds(float x, float y) {
        float width = line.getScaleX();
        float height = line.getScaleY();
        float minx = line.getPosition()[0] - width / 2.0f;
        float miny = line.getPosition()[1] - height / 1.8f;
        float maxx = line.getPosition()[0] + width / 2.0f;
        float maxy = line.getPosition()[1] + height / 1.8f;
        if (!(x < minx || x > maxx || y < miny || y > maxy)) {
            return true;
        }
        return false;
    }
}
