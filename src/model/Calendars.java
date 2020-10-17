package model;

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
     * Optionally Crop given calendar from top
     * @param bound
     * @param cal
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
     * Checks if have common part
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

    public static double takeMaxOfBeg(Time t1, Time t2){
        if(t1 == null)return t2.beg;
        else if(t2 == null)return t1.beg;

        return Math.max(t1.beg, t2.beg);
    }

    public static double takeMinOfEnd(Time t1, Time t2){
        if(t1 == null)return t2.end;
        else if(t2 == null)return t1.end;

        return Math.min(t1.end, t2.end);
    }


    /**
     * source calendar is turned to list of meetings and put into ArrayList list
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

    public static double periodLength(Time date){
        if(date.end < date.beg)return -1;

        return date.end - date.beg;
    }

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

    public static ArrayList<String> printAndReturnFormatted(ArrayList<Time> calendar){

        if(calendar == null) return null;
        ArrayList<String> resultFormatted = changeFormatToString(calendar);
        int i = 1;
        for(String s : resultFormatted) {
            System.out.println("meeting nr " + i + " " + s);
            i++;
        }
        return resultFormatted;
    }

    public static void justPrintFormattedToString(ArrayList<String> resultFormated){

        int i = 1;
        if(resultFormated == null) System.out.println("null");
        else if( resultFormated.size() == 0) System.out.println("size == 0");
        else
        for(String s : resultFormated) {
            System.out.println("meeting nr " + i + " " + s);
            i++;
        }
    }
}
