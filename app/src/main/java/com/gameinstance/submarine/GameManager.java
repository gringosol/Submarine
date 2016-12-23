package com.gameinstance.submarine;

import android.content.Context;
import android.content.SharedPreferences;

import com.gameinstance.submarine.utils.TextureHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    static Camera camera;
    static int levelId = 0;
    private static final String DEFAULT_SAVE = "quicksave";

    public static void initGame(final GameRenderer renderer) {
        scene = new Scene(renderer);
        GameManager.renderer = renderer;
        addLayers();
        texPrimitive = renderer.createPrimitiveTextured();
        Primitive colPrimitive = renderer.createPrimitiveColored();
        movablePrimitiveMap = new HashMap<>();
        movablePrimitiveMap.put(renderer.getProgramHandle("SimpleProgramHandle"), colPrimitive);
        movablePrimitiveMap.put(renderer.getProgramHandle("DefaultProgramHandle"), texPrimitive);
        camera = new Camera();
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

    }

    public static void setCamera() {
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
        Button saveButton = new Button(renderer, new int [] {R.drawable.save, R.drawable.save1},
                movablePrimitiveMap, 0.5f, 0.5f, new Button.ClickListener() {
            @Override
            public void onClick() {
                saveGame(DEFAULT_SAVE);
            }
        }, new float[] {0.95f, 0.75f});
        Button loadButton = new Button(renderer, new int [] {R.drawable.load, R.drawable.load1},
                movablePrimitiveMap, 0.5f, 0.5f, new Button.ClickListener() {
            @Override
            public void onClick() {
                loadGame(DEFAULT_SAVE);
            }
        }, new float[] {1.5f, 0.75f});
        Layer hud = scene.getLayer("hud");
        hud.addSprite(stopButton);
        hud.addSprite(emergeButton);
        hud.addSprite(plungeButton);
        hud.addSprite(nextLevelButton);
        hud.addSprite(saveButton);
        hud.addSprite(loadButton);
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
        scene.getLayer("landscape_back").clear();
        scene.getLayer("mobs_back").clear();
        scene.getLayer("landscape").clear();
        scene.getLayer("submarines").clear();
        scene.getLayer("ships_and_tanks").clear();
        scene.getLayer("aircrafts").clear();
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

    public static void setLevelId(int i) {
        levelId = i;
    }


    public static void saveGame(String filename) {
        JSONObject savedData = new JSONObject();
        try {
            savedData.put("levelId", levelId);
            JSONObject submarine = new JSONObject();
            submarine.put("x", submarineMovable.getSprite().getPosition()[0]);
            submarine.put("y", submarineMovable.getSprite().getPosition()[1]);
            submarine.put("angle", submarineMovable.getAngle());
            savedData.put("submarine", submarine);
        } catch (JSONException e) {
            throw new RuntimeException("Ошибка сохраненя json");
        }
        String s  = savedData.toString();
        SharedPreferences sharedPreferences = GameActivity.getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(filename, s);
        editor.apply();
    }

    public static void loadGame(String filename) {
        String s;
        SharedPreferences sharedPreferences = GameActivity.getActivity().getPreferences(Context.MODE_PRIVATE);
        s = sharedPreferences.getString(filename, "");
        if (!s.equals("")) {
            try {
                JSONObject loadedData = new JSONObject(s);
                final int levId = loadedData.getInt("levelId");
                JSONObject submarine = loadedData.getJSONObject("submarine");
                final float x = (float)submarine.getDouble("x");
                final float y = (float)submarine.getDouble("y");
                final float angle = (float)submarine.getDouble("angle");

                renderer.getSurfaceView().queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        if (levId != levelId) {
                            clearLevel();
                            currentLevel++;
                            LevelLoader.loadLevel(GameActivity.getActivity(), levId);
                        }
                        submarineMovable.getSprite().setPosition(x, y);
                        submarineMovable.setAngle(angle);
                        submarineMovable.setTarget(new float[] {x, y});
                    }
                });

            } catch (JSONException e) {
                throw new RuntimeException("Ошибка чтения json");
            }
        }
    }

    public static Submarine getSubmarineMovable() {
        return submarineMovable;
    }
}
