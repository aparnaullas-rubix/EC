package verifier;
import java.io.IOException;
import java.util.ArrayList;

public class SplitShares {

    public static ArrayList recombine(ArrayList inp) throws IOException
    {
        int i;
        String t1,t2;
        ArrayList temp = new ArrayList<String>();
        Interact4tree temp1 = new Interact4tree("tempdata");
        for (i = 0; i <inp.size()-1 ; i+=2)
        {
            t1 = (String) inp.get(i);
            t2 = (String) inp.get(i+1);
            temp.add(temp1.getback(t1,t2,false));
        }
        if(temp.size()>1)
            temp = recombine(temp);
        return temp;
    }
}
