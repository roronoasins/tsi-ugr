package src_gvgai;

import tools.Vector2d;

public class MyPair
{
    public int value;
    public int vector_index;

    public MyPair(int aValue, int aVector)
    {
        value   = aValue;
        vector_index = aVector;
    }

    public int value()   { return value; }
    public void setValue(int v) { value = v; }
    public int vector_index() { return vector_index; }
}
