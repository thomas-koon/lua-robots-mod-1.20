package com.example;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LuaScriptManager {
    private Globals luaGlobals;
    private RobotEntity robotEntity;
    private final ExecutorService executorService;

    public LuaScriptManager(RobotEntity robotEntity) {
        this.robotEntity = robotEntity;
        luaGlobals = JsePlatform.standardGlobals();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void executeLuaScript(String luaScript) {
        luaGlobals.set("robot", CoerceJavaToLua.coerce(robotEntity));
        Runnable luaRunnable = () -> {
            try {
                luaGlobals.load(luaScript).call();
            } catch (LuaError e) {
                // Handle Lua script execution errors here
                System.out.println("Lua Error: " + e.getMessage());
            }
        };
        executorService.submit(luaRunnable);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
