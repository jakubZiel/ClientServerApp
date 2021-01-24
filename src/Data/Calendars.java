package Data;

import java.util.ArrayList;
import java.lang.Math;

/**
 * Main logic and algorithm that takes to calendars and returns
 * common part that consists of intervals of previously set duration.
 * Class returns the earliest possible meetings.
 * Time complexity of the algorithm is O(N).
 */

public class Calendars {

    /**
     * Main function that aggregates all the functions of Calendars class to generate common part calendar and divide it into meetings.
     * It returns set of possible meetings in form of array of Time objects.
     * @param cal1 first calendar
     * @param cal2 second calendar
     * @param duration duration of the meeting
     * @return set of possible meetings
     */
    public static ArrayList<Time> solution (ArrayList<Time> cal1, ArrayList<Time> cal2, double duration){
        if(duration  ==  0 || cal1 == null || cal2 == null )return null;

        ArrayList<Time> result = new ArrayList<>();

        addTogether(cal1, cal2, result);

        ArrayList<Time> finalRes = new ArrayList<>();

        DivideIntoMeetings(finalRes, result, duration);

        return finalRes;
    }

    /**
     * Optionally Crop given calendar from bottom
     * @param bound
     * @param cal
     */

    /**
     * Optionally Crop given calendar from top. Calendar can not have Time object that has beg or end smaller than bound.
     * @param bound Lower time bound that trims input calendar
     * @param cal input calendar
     */
    private static void preProcessLower(Time bound, ArrayList<Time> cal){
        int index = 0;

        while(bound.beg > cal.get(index).beg){

            if(bound.beg > cal.get(index).beg && bound.beg < cal.get(index).end){
                cal.get(index).beg = bound.beg;
                index++;
            }else if(bound.beg > cal.get(index).end)cal.remove(index);
        }
    }

    /**
     * Optionally Crop given calendar from top. Calendar can not have Time object that has beg or end bigger than bound.
     * @param bound Upper time bound that trims input calendar
     * @param cal input calendar
     */

    private static void preProcessUpper(Time bound, ArrayList<Time> cal){
        int index = cal.size() - 1;

        while(bound.end < cal.get(index).end){

            if(bound.end > cal.get(index).beg && bound.end < cal.get(index).end)
                cal.get(index).end = bound.end;
                else if(bound.end < cal.get(index).beg)cal.remove(index);
            index--;
        }
    }

    /**
     * Main function of the algorithm, it sums up calendars cal1 and cal2 into result calendar that is the final output calendar equal to common part of input calendars.
     * @param cal1 first input calendar
     * @param cal2 second input calendar
     * @param result output calendar
     */
    private static void addTogether(ArrayList<Time> cal1, ArrayList<Time> cal2, ArrayList<Time> result){
        int index1, index2;
        double MaxB,MinE;
        index1 = index2 = 0;

        while(index1 < cal1.size() && index2 < cal2.size() ) {

            if(hasCommonPart(cal1.get(index1), cal2.get(index2))){
                //calculate the bounds of common part, because it exists
                MaxB = takeMaxOfBeg(cal1.get(index1), cal2.get(index2));
                MinE = takeMinOfEnd(cal1.get(index1), cal2.get(index2));
                //if current MaxB does not belong to current period then it means that current
                // period has redundant part <current.beg, MaxB> so we set the length of current period to MaxB
                if(cal1.get(index1).beg == MaxB ) cal2.get(index2).beg = MaxB;
                else cal1.get(index1).beg = MaxB;
                //add period to array of common parts
                result.add(new Time(MaxB, MinE));
                //adjust both dates beginnings to MinE, because that period has already been processed
                cal2.get(index2).beg = MinE;
                cal1.get(index1).beg = MinE;
                // if beg == end it means that period has been processed
                if(cal1.get(index1).beg == cal1.get(index1).end)index1++;
                if(cal2.get(index2).beg == cal2.get(index2).end)index2++;
            }
            else if(cal1.get(index1).end <= cal2.get(index2).beg)index1++;
                 else if(cal2.get(index2).end <= cal1.get(index1).beg)index2++;
        }
    }


    /**
     * Checks if Time objects have common part.
     *
     * @param t1 period 1
     * @param t2 period 2
     * @return
     */
    public static boolean hasCommonPart(Time t1, Time t2){

        if (t1 == null || t2 == null)return false;
        double beg = Math.max(t1.beg,t2.beg);
        double end = Math.min(t1.end,t2.end);
        return end > beg;
    }

    /**
     * Takes 2 Time objects and returns the earlier ending.
     * @param t1 Time object 1
     * @param t2 Time object 2
     * @return
     */
    public static double takeMaxOfBeg(Time t1, Time t2){
        if(t1 == null)return t2.beg;
        else if(t2 == null)return t1.beg;

        return Math.max(t1.beg, t2.beg);
    }

    /**
     * Takes 2 Time objects and returns the earlier ending.
     * @param t1 Time object 1
     * @param t2 Time object 2
     * @return
     */
    public static double takeMinOfEnd(Time t1, Time t2){
        if(t1 == null)return t2.end;
        else if(t2 == null)return t1.end;

        return Math.min(t1.end, t2.end);
    }


    /**
     * Source calendar is turned to list of meetings and put into ArrayList list.
     * @param list list of possible meetings
     * @param source calendar that is to be turned into meetings
     * @param length length of meeting
     */
    private static void DivideIntoMeetings(ArrayList<Time> list, ArrayList<Time> source, double length){

        int index = 0;

        if(source == null || length == 0 || list == null)return;

        while(index < source.size()){

            //if common part is  smaller than length check other common part else process
            if( periodLength(source.get(index)) < length) index++;
            else{   //add common part with sufficient length to list, and adjust source,
                // then process it again in  another iteration
                list.add(new Time(source.get(index).beg,source.get(index).beg + length));
                source.get(index).beg += length;
            }
        }
    }

    /**
     * Returns length of Time object (end - beg).
     *
     * @param date Time object
     * @return this.end - this.beg
     */
    public static double periodLength(Time date){
        if(date.end < date.beg)return -1;

        return date.end - date.beg;
    }

    /**
     * Takes calendar of types Time and returns calendar made of strings.
     * @param calendar  input calendar made of Time objects.
     * @return Calendar made of Strings.
     */
    public static ArrayList<String> changeFormatToString(ArrayList<Time> calendar){
        ArrayList<String> result = new ArrayList<>();

        int wholePart;
        double restPart;
        int wholePart2;
        double restPart2;
        int tmp;
        int tmp2;

        for(Time t : calendar){
            wholePart = (int)t.beg;
            restPart = (t.beg - wholePart)*60;
            tmp = (int)restPart;
            wholePart2 = (int)t.end;
            restPart2 = (t.end - wholePart2)*60;
            tmp2 = (int)restPart2;

            if(restPart < 10 && restPart2 < 10) result.add("[" + new String(String.valueOf(wholePart)) + ":" + "0" + String.valueOf(tmp) +
                    "," + String.valueOf(wholePart2) + ":" + "0" + String.valueOf(tmp2) +"]");
            else if(restPart2 < 10) result.add("[" + new String(String.valueOf(wholePart)) + ":" + String.valueOf(tmp) +
                    "," + String.valueOf(wholePart2) + ":" + "0" + String.valueOf(tmp2) +"]");
            else if(restPart < 10) result.add("[" + new String(String.valueOf(wholePart)) + ":" + "0" + String.valueOf(tmp) +
                    "," + String.valueOf(wholePart2) + ":" + String.valueOf(tmp2) +"]");
            else result.add("[" + new String(String.valueOf(wholePart)) + ":" + String.valueOf(tmp) +
                    "," + String.valueOf(wholePart2) + ":" + String.valueOf(tmp2) +"]");
        }

        return result;
    }

    /**
     * Prints formatted input calendar to terminal output.
     * @param resultFormatted input calendar of String objects.
     */

    public static void justPrintFormattedToString(ArrayList<String> resultFormatted){

        int i = 1;
        if(resultFormatted == null) System.out.println("null");
        else if( resultFormatted.size() == 0) System.out.println("size == 0");
        else
        for(String s : resultFormatted) {
            System.out.println("meeting nr " + i + " " + s);
            i++;
        }
    }
}
