import java.util.Comparator;
/**
 * Write a description of class SortbyRank here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SortbyRank implements Comparator<rank_number>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(rank_number a, rank_number b)
    {
        return (int)b.rank - (int)a.rank;
    }
}
   