package com.example.photobattle;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.util.color.Color;


public class LoadingScene extends Scene
{
    LoadingScene()
    {
        super();
        createScene();
    }

    public void createScene()
    {
        setBackground(new Background(Color.WHITE));
    }

    public void onBackKeyPressed()
    {
        return;
    }

    public void disposeScene()
    {

    }
}