package bobo.lb.pasteur.robotservice.dto;

import java.io.Serializable;

public class RobotInfo implements Serializable {

    private static final long serialVersionUID = -1848002223060441592L;
    private long id;

    private String host;

    private int line;

    private String station;

    private int trainId;

    public long getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getLine() {
        return line;
    }

    public String getStation() {
        return station;
    }

    public int getTrainId() {
        return trainId;
    }

    public RobotInfo(long id, String host, int line, String station, int trainId) {
        this.id = id;
        this.host = host;
        this.line = line;
        this.station = station;
        this.trainId = trainId;
    }
}
