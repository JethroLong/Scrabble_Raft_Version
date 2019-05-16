package app.Models;

import java.util.ArrayList;

public class  Team {
    public Team() {
    }

    public Team(int hostID, Users[] teamMember) {
        this.hostID = hostID;
        this.teamMember = teamMember;
    }

    public Team(int hostID, ArrayList<Users> teamMember) {
        this.hostID = hostID;
        this.teamMember = new Users[teamMember.size()];
        this.teamMember = teamMember.toArray(this.teamMember);
    }

    private int hostID;

    public int getHostID() {
        return hostID;
    }

    public void setHostID(int hostID) {
        this.hostID = hostID;
    }

    public Users[] getTeamMember() {
        return teamMember;
    }

    public void setTeamMember(Users[] teamMember) {
        this.teamMember = teamMember;
    }

    private Users[] teamMember;
}
