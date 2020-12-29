package model.Data;

import java.io.Serializable;


/**
 * Class for time representation with beginning and ending of represented period of time.
 */

public class Time implements Serializable{

    /**
     Beginning of period.
    */
    public double beg;
    /**
     Ending of period.
     */
    public double end;

    /**
     * Constructor that treats parameters as follows :
     * Whole part is treated as a hour and remaining fraction is treated as a fraction of an hour, resulting in getting remaining minutes.
     * @param beg Begging of the period.
     * @param end End of the period.
     */

    public Time(double beg, double end){

        this.beg = beg;
        this.end = end;
    }


    /**
     * Constructor that takes time in minutes and hours.
     *
     * @param hrs1 Beginning hour.
     * @param min1 Beginning minute.
     * @param hrs2 Ending hour.
     * @param min2 Ending minute.
     */
    public Time(int hrs1, int min1, int hrs2, int min2){

        beg = ((double)min1)/60;
        beg += hrs1;

        end = ((double)min2)/60;
        end += hrs2;
    }
}
