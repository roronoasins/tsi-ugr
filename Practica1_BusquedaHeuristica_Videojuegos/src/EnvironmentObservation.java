package src_gvgai;

public class EnvironmentObservation
{

	private ObservationEnum type;
	private int x;
	private int y;

	public EnvironmentObservation(int x, int y, ObservationEnum type)
	{
		this.type = type;
		this.x = x;
		this.y = y;

	}

	public EnvironmentObservation(core.game.Observation obs, int blockSize){
		int itype = obs.itype;

		switch(itype){
			case(0):
				type = ObservationEnum.WALL;
				break;
			case(4):
				type = ObservationEnum.GROUND;
				break;
			case(7):
				type = ObservationEnum.BOULDER;
				break;
			case(6):
				type = ObservationEnum.GEM;
				break;
			case(11):
				type = ObservationEnum.BAT;
				break;
			case(10):
				type = ObservationEnum.SCORPION;
				break;
			case(1):
				type = ObservationEnum.PLAYER;
				break;
			case(5):
				type = ObservationEnum.EXIT;
				break;
			}

		tools.Vector2d pos = obs.position;

		this.x = (int)(pos.x / blockSize);
		this.y = (int)(pos.y / blockSize);
    }

	public int x(){
        return x;
    }

    public int y(){
        return y;
    }

    public ObservationEnum getType(){
        return type;
    }

}
