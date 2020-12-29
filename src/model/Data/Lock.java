package model.Data;

/**
 * Class used as a synchronizing object.
 */
public class Lock{


    private int id;

    public Lock(int iD ){
        this.id = iD;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    /**
     * Return true if Lock is actually this object.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        else return false;
    }
}
