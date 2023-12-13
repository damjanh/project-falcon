package si.damjanh.falcon.engine;

import si.damjanh.falcon.engine.graph.MouseInput;

public interface IGameLogic {

    void init(Window window) throws Exception;

    void input(Window window);

    void update(float interval, MouseInput mouseInput);

    void render(Window window);

    void cleanup();
}
