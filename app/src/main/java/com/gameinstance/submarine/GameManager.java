package com.gameinstance.submarine;

import com.gameinstance.submarine.utils.TextureHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by gringo on 11.12.2016 20:16.
 *
 */
public class GameManager {
    static Scene scene;
    static GameRenderer renderer;
    private static float minX = -1.0f;
    private static float maxX = 1.0f;
    private static float minY = -1.0f;
    private static float maxY = 1.0f;
    static Primitive texPrimitive;
    static Map<Integer, Primitive> movablePrimitiveMap;
    static Submarine submarineMovable;
    static List<Integer> levelList = Arrays.asList(R.raw.testlevel, R.raw.testlevel2);
    static int currentLevel = 0;

    public static void initGame(final GameRenderer renderer) {
        scene = new Scene(renderer);
        GameManager.renderer = renderer;
        addLayers();
        texPrimitive = renderer.createPrimitiveTextured();
        Primitive colPrimitive = renderer.createPrimitiveColored();
        movablePrimitiveMap = new HashMap<>();
        movablePrimitiveMap.put(renderer.getProgramHandle("SimpleProgramHandle"), colPrimitive);
        movablePrimitiveMap.put(renderer.getProgramHandle("DefaultProgramHandle"), texPrimitive);

        LevelLoader.loadLevel(GameActivity.getActivity(), R.raw.testlevel);

        InputController.addTouchHandler(new TouchHandler(1) {
            @Override
            public boolean touch(int x, int y) {
                submarineMovable.setTarget(renderer.convertCoords(x, y, false));
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
        });
        addGui();
        Camera camera = new Camera();
        camera.setTarget(submarineMovable.getSprite());
        camera.setBounds(minX, maxX, minY, maxY);
        renderer.setCamera(camera);
    }

    public static Sprite [] createLandScape(int textureId, int pixelsPerUnit, float unitSize, Primitive primitive) {
        Map<Integer, Primitive> primitiveMap = new HashMap<>();
        primitiveMap.put(renderer.getProgramHandle("DefaultProgramHandle"), primitive);
        int [] texHandles = TextureHelper.loadTexture2(renderer.getActivityContext(), textureId, pixelsPerUnit);
        int n = texHandles[0];
        int m = texHandles[1];
        Sprite [] sprites = new Sprite[m * n];
        float left = -(n * unitSize) / 2.0f + 0.5f * unitSize;
        float top = (m * unitSize) / 2.0f - 0.5f * unitSize;
        for (int j = 0; j < m; j++) {
            for (int i = 0; i < n; i++) {
                sprites[j * n + i] = new Sprite(renderer, texHandles[j * n + i + 2], primitiveMap,
                        unitSize, new float[] {left + i * unitSize, top - j * unitSize});
            }
        }
        minX =  -(n * unitSize) / 2.0f;
        maxX =  (n * unitSize) / 2.0f;
        minY =  -(m * unitSize) / 2.0f;
        maxY =  (m * unitSize) / 2.0f;
        return sprites;
    }

    private static void addLayers() {
        scene.addLayerSet("BackBuffer", Arrays.asList("landscape_back", "mobs_back"));
        scene.addLayerSet("Front", Arrays.asList("landscape", "submarines", "ships_and_tanks",
                "aircrafts", "hud"));
        Layer landscape_back = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        scene.addLayer("landscape_back", landscape_back);
        Layer mobs_back = new Layer(renderer.getProgramHandle("SimpleProgramHandle"));
        mobs_back.setColor(new float[] {1.0f, 0.0f, 0.0f, 0.5f });
        scene.addLayer("mobs_back", mobs_back);
        Layer landscape = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        scene.addLayer("landscape", landscape);
        Layer submarines = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        scene.addLayer("submarines", submarines);
        Layer shipsAndTanks = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        scene.addLayer("ships_and_tanks", shipsAndTanks);
        Layer aircrafts = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        scene.addLayer("aircrafts", aircrafts);
        Layer hud = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        hud.isGui = true;
        scene.addLayer("hud", hud);
    }

    private static void addGui() {
        Button stopButton = new Button(renderer, new int [] {R.drawable.stop, R.drawable.stop1},
                movablePrimitiveMap, 0.5f, 0.5f, new Button.ClickListener() {
            @Override
            public void onClick() {
                submarineMovable.setMotionEnabled(false);
            }
        }, new float[] {1.5f, -0.75f});

        Button emergeButton = new Button(renderer, new int [] {R.drawable.emerge, R.drawable.emerge1},
                movablePrimitiveMap, 0.5f, 0.5f, new Button.ClickListener() {
            @Override
            public void onClick() {
                submarineMovable.emerge();
            }
        }, new float[] {0.95f, 0.0f});

        Button plungeButton = new Button(renderer, new int [] {R.drawable.plunge, R.drawable.plunge1},
                movablePrimitiveMap, 0.5f, 0.5f, new Button.ClickListener() {
            @Override
            public void onClick() {
                submarineMovable.plunge();
            }
        }, new float[] {1.5f, 0.0f});
        Button nextLevelButton = new Button(renderer, new int [] {R.drawable.nextlevel, R.drawable.nextlevel1},
                movablePrimitiveMap, 0.5f, 0.5f, new Button.ClickListener() {
            @Override
            public void onClick() {
                nextLevel();
            }
        }, new float[] {0.95f, -0.75f});
        Layer hud = scene.getLayer("hud");
        hud.addSprite(stopButton);
        hud.addSprite(emergeButton);
        hud.addSprite(plungeButton);
        hud.addSprite(nextLevelButton);
    }

    public static Scene getScene() {
        return scene;
    }

    public static Primitive getTexPrimitive() {
        return texPrimitive;
    }

    public static GameRenderer getRenderer() {
        return renderer;
    }

    public static Map<Integer, Primitive> getMovablePrimitiveMap() {
        return movablePrimitiveMap;
    }

    public static void setSubmarineMovable(Submarine submarine) {
        submarineMovable = submarine;
    }

    private static void clearLevel() {
        List<String> layerList = new ArrayList<>();
        for (Map.Entry<String, Layer> entry : scene.getLayers().entrySet()) {
            layerList.add(entry.getKey());
        }
        for (String layerName : layerList) {
            scene.getLayer(layerName).clear();
        }
    }

    private static void nextLevel() {
        renderer.setPaused(true);
        renderer.getSurfaceView().queueEvent(new Runnable() {
            @Override
            public void run() {

                clearLevel();
                currentLevel++;
                if (currentLevel >= levelList.size())
                    currentLevel = 0;
                LevelLoader.loadLevel(GameActivity.getActivity(), levelList.get(currentLevel));

            }
        });
        renderer.setPaused(false);
    }
}
