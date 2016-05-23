package com.photogame.imarena;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;


public class LoadingScene extends Scene
{
    Game activity;
    LoadingScene(Game activity)
    {
        super();
        this.activity =activity;
        createScene();
    }

    public void createScene()
    {
        setBackground(new Background(Color.WHITE));
        this.attachChild(activity.textLoading);
        textPosition(activity.textLoading);

    }

    public void onBackKeyPressed()
    {
        return;
    }

    public void disposeScene()
    {

    }

    private void textPosition(Text text)
    {
        text.setX(Game.CAMERA_WIDTH/2-text.getScaleCenterX());
        text.setY(Game.CAMERA_HEIGHT/2-text.getScaleCenterY());
    }
}