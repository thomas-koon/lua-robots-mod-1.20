package com.example;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaScriptManager {
    private Globals luaGlobals;
    private RobotEntity robotEntity;

    public LuaScriptManager(RobotEntity robotEntity) {
        this.robotEntity = robotEntity;
        luaGlobals = JsePlatform.standardGlobals();
    }

    public void executeLuaScript(String luaScript) {
        luaGlobals.set("robot", CoerceJavaToLua.coerce(robotEntity));
        try {
            System.out.println(luaGlobals.load(luaScript).call());
            luaGlobals.load(luaScript).call();
        } catch (LuaError e) {
            // Handle Lua script execution errors here
            System.out.println("Lua Error: " + e.getMessage());
        }
    }
}
