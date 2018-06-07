package com.ingeniapps.dicmax.loader;

import com.ingeniapps.dicmax.loader.sprite.Sprite;
import com.ingeniapps.dicmax.loader.style.ChasingDots;
import com.ingeniapps.dicmax.loader.style.Circle;
import com.ingeniapps.dicmax.loader.style.CubeGrid;
import com.ingeniapps.dicmax.loader.style.DoubleBounce;
import com.ingeniapps.dicmax.loader.style.FadingCircle;
import com.ingeniapps.dicmax.loader.style.FoldingCube;
import com.ingeniapps.dicmax.loader.style.MultiplePulse;
import com.ingeniapps.dicmax.loader.style.MultiplePulseRing;
import com.ingeniapps.dicmax.loader.style.Pulse;
import com.ingeniapps.dicmax.loader.style.PulseRing;
import com.ingeniapps.dicmax.loader.style.RotatingCircle;
import com.ingeniapps.dicmax.loader.style.RotatingPlane;
import com.ingeniapps.dicmax.loader.style.ThreeBounce;
import com.ingeniapps.dicmax.loader.style.WanderingCubes;
import com.ingeniapps.dicmax.loader.style.Wave;

/**
 * Created by ybq.
 */
public class SpriteFactory {

    public static Sprite create(Tipo_Loader style) {
        Sprite sprite = null;
        switch (style) {
            case ROTATING_PLANE:
                sprite = new RotatingPlane();
                break;
            case DOUBLE_BOUNCE:
                sprite = new DoubleBounce();
                break;
            case WAVE:
                sprite = new Wave();
                break;
            case WANDERING_CUBES:
                sprite = new WanderingCubes();
                break;
            case PULSE:
                sprite = new Pulse();
                break;
            case CHASING_DOTS:
                sprite = new ChasingDots();
                break;
            case THREE_BOUNCE:
                sprite = new ThreeBounce();
                break;
            case CIRCLE:
                sprite = new Circle();
                break;
            case CUBE_GRID:
                sprite = new CubeGrid();
                break;
            case FADING_CIRCLE:
                sprite = new FadingCircle();
                break;
            case FOLDING_CUBE:
                sprite = new FoldingCube();
                break;
            case ROTATING_CIRCLE:
                sprite = new RotatingCircle();
                break;
            case MULTIPLE_PULSE:
                sprite = new MultiplePulse();
                break;
            case PULSE_RING:
                sprite = new PulseRing();
                break;
            case MULTIPLE_PULSE_RING:
                sprite = new MultiplePulseRing();
                break;
            default:
                break;
        }
        return sprite;
    }
}
