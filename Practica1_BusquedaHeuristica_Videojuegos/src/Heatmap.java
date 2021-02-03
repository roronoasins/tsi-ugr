package src_gvgai;

import java.util.ArrayList;

import ontology.Types;
import tools.Vector2d;

import core.game.StateObservation;
import core.game.Observation;

import javax.swing.plaf.nimbus.State;

public class Heatmap
{
    ArrayList<core.game.Observation> grid[][];
    public MyPair[][] heatmatrix;
    ArrayList<Vector2d> enemies;
    StateObservation actual_state;
    int blockSize;
    private static final int enemy_value = 4;

    public enum scapeEnum
    {
        scapeFromLeft, scapeFromRight, scapeFromTop, scapeFromBottom, safe;
    }

    public Heatmap(StateObservation stateObs)
    {
        this.grid = stateObs.getObservationGrid();
        heatmatrix = new MyPair[grid.length][grid[0].length];
        initialize_heatmatrix();
        enemies = new ArrayList<Vector2d>();
        actual_state = stateObs;
        this.blockSize = stateObs.getBlockSize();

    }

    private void initialize_heatmatrix()
    {
        for(int i=0; i < grid.length; i++)
            for(int j=0; j < grid[0].length; j++)
                heatmatrix[i][j] = new MyPair(0,0);

    }

    protected void getEnemies(StateObservation stateObs){
        //this.updateState(stateObs);
        ArrayList<core.game.Observation>[] npcs = stateObs.getNPCPositions();

       // System.out.println(npcs.length);
        for (int i = 0; i < npcs.length; i++){

            //enemies.add(new Vector2d((int)(obs.position.x/blockSize), (int)(obs.position.y/blockSize)));

            for (core.game.Observation obs : npcs[i])
                enemies.add(new Vector2d((int)(obs.position.x/this.blockSize), (int)(obs.position.y/this.blockSize)));

        }
    }

    public ArrayList<Vector2d> listOfEnemies()
    {
        return this.enemies;
    }

    public void printEnemies()
    {
        for(int i=0; i < enemies.size(); i++)
        {
            System.out.println("Enemy  "+i+": ("+enemies.get(i).x+", "+enemies.get(i).y+")");
        }
    }

    public void updateState(StateObservation stateObs)
    {
        this.actual_state = stateObs;
        this.updateEnemies(stateObs);
        this.updateHeatmapMatrix();
    }

    public void updateEnemies(StateObservation stateObs)
    {
        enemies.clear();
        ArrayList<core.game.Observation>[] npcs = stateObs.getNPCPositions();
        for (int i = 0; i < npcs.length; i++){
            for (core.game.Observation obs : npcs[i])
            {
                enemies.add(new Vector2d((int)(obs.position.x/this.blockSize), (int)(obs.position.y/this.blockSize)));
            }
        }
    }

    public void updateHeatmapMatrix()
    {
        // each enemy...
        for(int i=0; i < this.enemies.size(); i++)
        {
            int enemy_x = (int)this.enemies.get(i).x;
            int enemy_y = (int)this.enemies.get(i).y;
            // core
            heatmatrix[enemy_x][enemy_y] = new MyPair(this.enemy_value, i);
            // each direction/value
            for(int j=1; j < this.enemy_value; j++)
            {
                // only cares about the places that are passable
                // north
                if(enemy_y+j < heatmatrix[0].length)
                    if(this.grid[enemy_x][enemy_y+j].isEmpty())
                    {
                        if(enemy_value-j > heatmatrix[enemy_x][enemy_y+j].value)
                            heatmatrix[enemy_x][enemy_y+j] = new MyPair(enemy_value-j + heatmatrix[enemy_x][enemy_y+j].value, i);  // check that actual value is not higher than new, then insert; if enemy_value-j < heatmatrix[][] -> update
                        else heatmatrix[enemy_x][enemy_y+j].value += enemy_value-j;
                    }else if(this.grid[enemy_x][enemy_y+j].get(0).itype != 0)
                            if(enemy_value-j > heatmatrix[enemy_x][enemy_y+j].value)
                            {
                                heatmatrix[enemy_x][enemy_y+j].value += enemy_value-j;
                                heatmatrix[enemy_x][enemy_y+j].vector_index = i;
                            }
                            else heatmatrix[enemy_x][enemy_y+j].value += enemy_value-j;
                // south
                if(enemy_y-j >= 0)
                    if(this.grid[enemy_x][enemy_y-j].isEmpty())
                    {
                        if(enemy_value-j > heatmatrix[enemy_x][enemy_y-j].value)
                        {
                            heatmatrix[enemy_x][enemy_y-j].value += enemy_value-j;
                            heatmatrix[enemy_x][enemy_y-j].vector_index = i;
                        }
                        else heatmatrix[enemy_x][enemy_y-j].value += enemy_value-j;
                    }else if(this.grid[enemy_x][enemy_y-j].get(0).itype != 0)
                        if(enemy_value-j > heatmatrix[enemy_x][enemy_y-j].value)
                        {
                            heatmatrix[enemy_x][enemy_y-j].value += enemy_value-j;
                            heatmatrix[enemy_x][enemy_y-j].vector_index = i;
                        }
                        else heatmatrix[enemy_x][enemy_y-j].value += enemy_value-j;
                // east
                if(enemy_x+j < heatmatrix.length)
                {
                    if(this.grid[enemy_x+j][enemy_y].isEmpty())
                    {
                        if(enemy_value-j > heatmatrix[enemy_x+j][enemy_y].value)
                        {
                            heatmatrix[enemy_x+j][enemy_y].value += enemy_value-j;
                            heatmatrix[enemy_x+j][enemy_y].vector_index = i;
                        }
                        else heatmatrix[enemy_x+j][enemy_y].value += enemy_value-j;
                    }else if(this.grid[enemy_x+j][enemy_y].get(0).itype != 0)
                        if(enemy_value-j > heatmatrix[enemy_x+j][enemy_y].value)
                        {
                            heatmatrix[enemy_x+j][enemy_y].value += enemy_value-j;
                            heatmatrix[enemy_x+j][enemy_y].vector_index = i;
                        }
                        else heatmatrix[enemy_x+j][enemy_y].value += enemy_value-j;
                    // diagonal(east-side)
                    for(int k=1; k < heatmatrix[enemy_x+j][enemy_y].value; k++)
                    {
                        // top side
                        if(enemy_y+j < heatmatrix[0].length)
                            if(this.grid[enemy_x+j][enemy_y+j].isEmpty())
                            {
                                if(enemy_value-j-k > heatmatrix[enemy_x+j][enemy_y+j].value)
                                {
                                    heatmatrix[enemy_x+j][enemy_y+j].value += enemy_value-j-k;
                                    heatmatrix[enemy_x+j][enemy_y+j].vector_index = i;
                                }
                                else heatmatrix[enemy_x+j][enemy_y+j].value += enemy_value-j-k;

                            }else if(this.grid[enemy_x+j][enemy_y+j].get(0).itype != 0)
                                if(enemy_value-j-k > heatmatrix[enemy_x+j][enemy_y+j].value)
                                {
                                    heatmatrix[enemy_x+j][enemy_y+j].value += enemy_value-j-k;
                                    heatmatrix[enemy_x+j][enemy_y+j].vector_index = i;
                                }
                                else heatmatrix[enemy_x+j][enemy_y+j].value += enemy_value-j-k;
                        // bottom side
                        if(enemy_y-j > 0)
                            if(this.grid[enemy_x+j][enemy_y-j].isEmpty())
                            {
                                if(enemy_value-j-k > heatmatrix[enemy_x+j][enemy_y-j].value)
                                {
                                    heatmatrix[enemy_x+j][enemy_y-j].value += enemy_value-j-k;
                                    heatmatrix[enemy_x+j][enemy_y-j].vector_index = i;
                                }
                                else heatmatrix[enemy_x+j][enemy_y-j].value += enemy_value-j-k;

                            }else if(this.grid[enemy_x+j][enemy_y-j].get(0).itype != 0)
                                if(enemy_value-j-k > heatmatrix[enemy_x+j][enemy_y-j].value)
                                {
                                    heatmatrix[enemy_x+j][enemy_y-j].value += enemy_value-j-k;
                                    heatmatrix[enemy_x+j][enemy_y-j].vector_index = i;
                                }
                                else heatmatrix[enemy_x+j][enemy_y-j].value += enemy_value-j-k;
                    }


                }

                // west
                if(enemy_x-j >= 0)
                {
                    if(this.grid[enemy_x-j][enemy_y].isEmpty())
                    {
                        if(enemy_value-j > heatmatrix[enemy_x-j][enemy_y].value)
                        {
                            heatmatrix[enemy_x-j][enemy_y].value += enemy_value-j;
                            heatmatrix[enemy_x-j][enemy_y].vector_index = i;
                        }
                        else heatmatrix[enemy_x-j][enemy_y].value += enemy_value-j;
                    }else if(this.grid[enemy_x-j][enemy_y].get(0).itype != 0)
                        if(enemy_value-j > heatmatrix[enemy_x-j][enemy_y].value)
                        {
                            heatmatrix[enemy_x-j][enemy_y].value += enemy_value-j;
                            heatmatrix[enemy_x-j][enemy_y].vector_index = i;
                        }
                        else heatmatrix[enemy_x-j][enemy_y].value += enemy_value-j;

                    // diagonal(west-side)
                    for(int k=1; k < heatmatrix[enemy_x-j][enemy_y].value; k++)
                    {
                        // top side
                        if(enemy_y+j < heatmatrix[0].length)
                            if(this.grid[enemy_x-j][enemy_y+j].isEmpty())
                            {
                                if(enemy_value-j-k > heatmatrix[enemy_x-j][enemy_y+j].value)
                                {
                                    heatmatrix[enemy_x-j][enemy_y+j].value += enemy_value-j-k;
                                    heatmatrix[enemy_x-j][enemy_y+j].vector_index = i;
                                }
                                else heatmatrix[enemy_x-j][enemy_y+j].value += enemy_value-j-k;

                            }else if(this.grid[enemy_x-j][enemy_y+j].get(0).itype != 0)
                                if(enemy_value-j-k > heatmatrix[enemy_x-j][enemy_y+j].value)
                                {
                                    heatmatrix[enemy_x-j][enemy_y+j].value += enemy_value-j-k;
                                    heatmatrix[enemy_x-j][enemy_y+j].vector_index = i;
                                }
                                else heatmatrix[enemy_x-j][enemy_y+j].value += enemy_value-j-k;
                        // bottom side
                        if(enemy_y-j >   0)
                            if(this.grid[enemy_x-j][enemy_y-j].isEmpty())
                            {
                                if(enemy_value-j-k > heatmatrix[enemy_x-j][enemy_y-j].value)
                                {
                                    heatmatrix[enemy_x-j][enemy_y-j].value += enemy_value-j-k;
                                    heatmatrix[enemy_x-j][enemy_y-j].vector_index = i;
                                }
                                else heatmatrix[enemy_x-j][enemy_y-j].value += enemy_value-j-k;

                            }else if(this.grid[enemy_x-j][enemy_y-j].get(0).itype != 0)
                                if(enemy_value-j-k > heatmatrix[enemy_x-j][enemy_y-j].value)
                                {
                                    heatmatrix[enemy_x-j][enemy_y-j].value += enemy_value-j-k;
                                    heatmatrix[enemy_x-j][enemy_y-j].vector_index = i;
                                }
                                else heatmatrix[enemy_x-j][enemy_y-j].value += enemy_value-j-k;
                    }
                }
            }

        }
    }

    public ArrayList<Types.ACTIONS> scapeFrom(Vector2d avatar, OrientationEnum orientation)
    {
        ArrayList<Types.ACTIONS> actions = new ArrayList<Types.ACTIONS>();
        scapeEnum scape = scapeEnum.safe;

        // if inside of enemy heatzone
        if(heatmatrix[(int)avatar.x][(int)avatar.y].value > 0)
        {
            int enemy_index = heatmatrix[(int)avatar.x][(int)avatar.y].vector_index;
            if(((int)(enemies.get(enemy_index).x - avatar.x)) > 0)
                scape = scapeEnum.scapeFromRight;
            else if(((int)(enemies.get(enemy_index).x - avatar.x)) < 0)
                scape = scapeEnum.scapeFromLeft;
            if(((int)(enemies.get(enemy_index).y - avatar.y)) > 0)
                   scape = scapeEnum.scapeFromBottom;
               else if(((int)(enemies.get(enemy_index).y - avatar.y)) < 0)
                   scape = scapeEnum.scapeFromTop;

            switch (scape)
            {
                case scapeFromRight:
                   // System.out.println(("runFromRight"));
                    return this.scapeFromRight(avatar, orientation);
                    //break;
                case scapeFromLeft:
                   // System.out.println(("runFromLeft"));
                    return this.scapeFromLeft(avatar, orientation);
                    //break;
                case scapeFromTop:
                   // System.out.println(("RunFromTop"));
                    return this.scapeFromTop(avatar, orientation);
                    //break;
                case scapeFromBottom:
                    //System.out.println(("RunFromBot"));
                    return this.scapeFromBottom(avatar, orientation);
                    //break;
                default:
                    break;
            }
        }
        return actions;
    }

    public ArrayList<Types.ACTIONS> scapeFromLeft(Vector2d avatar, OrientationEnum orientation)
    {
        ArrayList<Types.ACTIONS> actions = new ArrayList<Types.ACTIONS>();
        Types.ACTIONS action = Types.ACTIONS.ACTION_NIL;
        int n_actions = 0;

        if(this.grid[(int) avatar.x+1][(int) avatar.y].isEmpty())
        {
            if(heatmatrix[(int) avatar.x+1][(int) avatar.y].value == 0)
            {
                if (orientation != OrientationEnum.E)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_RIGHT;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);

        }else if (this.grid[(int) avatar.x + 1][(int) avatar.y].get(0).itype != 0)
        {
            if(heatmatrix[(int) avatar.x+1][(int) avatar.y].value == 0)
            {
                if (orientation != OrientationEnum.E)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_RIGHT;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x][(int)avatar.y+1].isEmpty())
        {
            if(heatmatrix[(int) avatar.x][(int) avatar.y+1].value == 0)
            {
                if (orientation != OrientationEnum.S)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_DOWN;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x][(int)avatar.y+1].get(0).itype != 0)
        {
            if(heatmatrix[(int) avatar.x][(int) avatar.y+1].value == 0)
            {
                if (orientation != OrientationEnum.S)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_DOWN;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x][(int)avatar.y-1].isEmpty())
        {
            if(heatmatrix[(int) avatar.x][(int) avatar.y-1].value == 0)
            {
                if (orientation != OrientationEnum.N)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_UP;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x][(int)avatar.y-1].get(0).itype != 0)
        {
            if(heatmatrix[(int) avatar.x][(int) avatar.y-1].value == 0)
            {
                if (orientation != OrientationEnum.N)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_UP;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }

        // add 'n' 'type' actions to actions data
        for(int i=0; i < n_actions; i++)
            actions.add(action);
        return actions;
    }

    public ArrayList<Types.ACTIONS> scapeFromRight(Vector2d avatar, OrientationEnum orientation)
    {
        ArrayList<Types.ACTIONS> actions = new ArrayList<Types.ACTIONS>();
        int n_actions = 0;
        Types.ACTIONS action = Types.ACTIONS.ACTION_NIL;

        if(this.grid[(int) avatar.x-1][(int) avatar.y].isEmpty())
        {
            if(heatmatrix[(int) avatar.x-1][(int) avatar.y].value == 0)
            {
                if (orientation != OrientationEnum.W)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_LEFT;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);

        }else if (this.grid[(int) avatar.x-1][(int) avatar.y].get(0).itype != 0)
        {
            if(heatmatrix[(int) avatar.x-1][(int) avatar.y].value == 0)
            {
                if (orientation != OrientationEnum.W)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_LEFT;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x][(int)avatar.y+1].isEmpty())
        {
            if(heatmatrix[(int) avatar.x][(int) avatar.y+1].value == 0)
            {
                if (orientation != OrientationEnum.S)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_DOWN;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x][(int)avatar.y+1].get(0).itype != 0)
        {
            if(heatmatrix[(int) avatar.x][(int) avatar.y+1].value == 0)
            {
                if (orientation != OrientationEnum.S)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_DOWN;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x][(int)avatar.y-1].isEmpty())
        {
            if(heatmatrix[(int) avatar.x][(int) avatar.y-1].value == 0)
            {
                if (orientation != OrientationEnum.N)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_UP;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x][(int)avatar.y-1].get(0).itype != 0)
        {
            if(heatmatrix[(int) avatar.x][(int) avatar.y-1].value == 0)
            {
                if (orientation != OrientationEnum.N)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_UP;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }

        for(int i=0; i < n_actions; i++)
            actions.add(action);
        return actions;
    }

    public ArrayList<Types.ACTIONS> scapeFromTop(Vector2d avatar, OrientationEnum orientation)
    {
        ArrayList<Types.ACTIONS> actions = new ArrayList<Types.ACTIONS>();
        // check if der, if not top if not bot
        int n_actions = 0;
        Types.ACTIONS action = Types.ACTIONS.ACTION_NIL;

        if(this.grid[(int) avatar.x][(int) avatar.y+1].isEmpty())
        {
            if(heatmatrix[(int) avatar.x][(int) avatar.y+1].value == 0)
            {
                if (orientation != OrientationEnum.S)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_DOWN;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);

        }else if (this.grid[(int) avatar.x][(int) avatar.y+1].get(0).itype != 0)
        {
            if(heatmatrix[(int) avatar.x][(int) avatar.y+1].value == 0)
            {
                if (orientation != OrientationEnum.S)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_DOWN;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x+1][(int)avatar.y].isEmpty())
        {
            if(heatmatrix[(int) avatar.x+1][(int) avatar.y].value == 0)
            {
                if (orientation != OrientationEnum.E)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_RIGHT;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x+1][(int)avatar.y].get(0).itype != 0)
        {
            if(heatmatrix[(int) avatar.x+1][(int) avatar.y].value == 0)
            {
                if (orientation != OrientationEnum.E)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_RIGHT;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x-1][(int)avatar.y].isEmpty())
        {
            if(heatmatrix[(int) avatar.x-1][(int) avatar.y].value == 0)
            {
                if (orientation != OrientationEnum.W)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_LEFT;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x-1][(int)avatar.y].get(0).itype != 0)
        {
            if(heatmatrix[(int) avatar.x-1][(int) avatar.y].value == 0)
            {
                if (orientation != OrientationEnum.W)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_LEFT;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }

        for(int i=0; i < n_actions; i++)
            actions.add(action);
        return actions;
    }
    public ArrayList<Types.ACTIONS> scapeFromBottom(Vector2d avatar, OrientationEnum orientation)
    {
        ArrayList<Types.ACTIONS> actions = new ArrayList<Types.ACTIONS>();
        // check if der, if not top if not bot
        int n_actions = 0;
        Types.ACTIONS action = Types.ACTIONS.ACTION_NIL;

        if(this.grid[(int) avatar.x][(int) avatar.y-1].isEmpty())
        {
            if(heatmatrix[(int) avatar.x][(int) avatar.y-1].value == 0)
            {
                if (orientation != OrientationEnum.N)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_UP;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);

        }else if (this.grid[(int) avatar.x][(int) avatar.y-1].get(0).itype != 0)
        {
            if(heatmatrix[(int) avatar.x][(int) avatar.y-1].value == 0)
            {
                if (orientation != OrientationEnum.N)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_UP;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x+1][(int)avatar.y].isEmpty())
        {
            if(heatmatrix[(int) avatar.x+1][(int) avatar.y].value == 0)
            {
                if (orientation != OrientationEnum.E)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_RIGHT;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x+1][(int)avatar.y].get(0).itype != 0)
        {
            if(heatmatrix[(int) avatar.x+1][(int) avatar.y].value == 0)
            {
                if (orientation != OrientationEnum.E)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_RIGHT;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x-1][(int)avatar.y].isEmpty())
        {
            if(heatmatrix[(int) avatar.x-1][(int) avatar.y].value == 0)
            {
                if (orientation != OrientationEnum.W)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_LEFT;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }else if(this.grid[(int)avatar.x-1][(int)avatar.y].get(0).itype != 0)
        {
            if(heatmatrix[(int) avatar.x-1][(int) avatar.y].value == 0)
            {
                if (orientation != OrientationEnum.W)
                    n_actions++;
                n_actions++;
                action = Types.ACTIONS.ACTION_LEFT;
            }else actions = scapeFromMultipleEnemies(avatar, orientation);
        }

        for(int i=0; i < n_actions; i++)
            actions.add(action);
        return actions;
    }

    // check each possible move and go to minimal enemy_value in heatmatrix[][]
    public ArrayList<Types.ACTIONS> scapeFromMultipleEnemies(Vector2d avatar, OrientationEnum orientation)
    {
        ArrayList<Types.ACTIONS> actions = new ArrayList<Types.ACTIONS>();
        Types.ACTIONS action = Types.ACTIONS.ACTION_NIL;
        int n_actions = 0;
        int actual_heat;
        int minimal_heat = 50;
        //System.out.println("go to minimal heat");

        if(this.grid[(int) avatar.x+1][(int) avatar.y].isEmpty())
        {
            actual_heat = heatmatrix[(int) avatar.x+1][(int) avatar.y].value;
            if(actual_heat < minimal_heat)
            {
                if (orientation != OrientationEnum.E)
                    n_actions = 2;
                else n_actions = 1;
                action = Types.ACTIONS.ACTION_RIGHT;

                minimal_heat = actual_heat;
            }
        }else if (this.grid[(int) avatar.x+1][(int) avatar.y].get(0).itype != 0)
        {
            actual_heat = heatmatrix[(int) avatar.x+1][(int) avatar.y].value;
            if(actual_heat < minimal_heat)
            {
                if (orientation != OrientationEnum.E)
                    n_actions = 2;
                else n_actions = 1;
                action = Types.ACTIONS.ACTION_RIGHT;

                minimal_heat = actual_heat;
            }
        }

        if(this.grid[(int) avatar.x-1][(int) avatar.y].isEmpty())
        {
            actual_heat = heatmatrix[(int) avatar.x-1][(int) avatar.y].value;
            if(actual_heat < minimal_heat)
            {
                if (orientation != OrientationEnum.W)
                    n_actions = 2;
                else n_actions = 1;
                action = Types.ACTIONS.ACTION_LEFT;

                minimal_heat = actual_heat;
            }
        }else if (this.grid[(int) avatar.x-1][(int) avatar.y].get(0).itype != 0)
        {
            actual_heat = heatmatrix[(int) avatar.x-1][(int) avatar.y].value;
            if(actual_heat < minimal_heat)
            {
                if (orientation != OrientationEnum.W)
                    n_actions = 2;
                else n_actions = 1;
                action = Types.ACTIONS.ACTION_LEFT;

                minimal_heat = actual_heat;
            }
        }

        if(this.grid[(int)avatar.x][(int)avatar.y+1].isEmpty())
        {
            actual_heat = heatmatrix[(int) avatar.x][(int) avatar.y+1].value;
            if(actual_heat < minimal_heat)
            {
                if (orientation != OrientationEnum.S)
                    n_actions = 2;
                else n_actions = 1;
                action = Types.ACTIONS.ACTION_DOWN;

                minimal_heat = actual_heat;
            }
        }else if(this.grid[(int)avatar.x][(int)avatar.y+1].get(0).itype != 0)
        {
            actual_heat = heatmatrix[(int) avatar.x][(int) avatar.y+1].value;
            if(actual_heat < minimal_heat)
            {
                if (orientation != OrientationEnum.S)
                    n_actions = 2;
                else n_actions = 1;
                action = Types.ACTIONS.ACTION_DOWN;

                minimal_heat = actual_heat;
            }
        }

        if(this.grid[(int)avatar.x][(int)avatar.y-1].isEmpty())
        {
            actual_heat = heatmatrix[(int) avatar.x][(int) avatar.y-1].value;
            if(actual_heat < minimal_heat)
            {
                if (orientation != OrientationEnum.N)
                    n_actions = 2;
                else n_actions = 1;
                action = Types.ACTIONS.ACTION_UP;

                minimal_heat = actual_heat;
            }
        }else if(this.grid[(int)avatar.x][(int)avatar.y-1].get(0).itype != 0)
        {
            actual_heat = heatmatrix[(int) avatar.x][(int) avatar.y-1].value;
            if(actual_heat < minimal_heat)
            {
                if (orientation != OrientationEnum.N)
                    n_actions = 2;
                else n_actions = 1;
                action = Types.ACTIONS.ACTION_UP;

                minimal_heat = actual_heat;
            }
        }

        for(int i=0; i < n_actions; i++)
            actions.add(action);
        return actions;
    }


    public void printHeapmatrix()
    {
        for(int i=0; i < heatmatrix.length; i++)
            for(int j=0; j < heatmatrix[i].length; j++)
                System.out.println(heatmatrix[i][j]);
    }

}
