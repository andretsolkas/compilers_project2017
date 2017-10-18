import java.util.LinkedList;

class ScopeTemp {

    int scope;
    LinkedList<Temp> temps;


    ScopeTemp(LinkedList<Temp> tmps) {
        scope = 0;
        temps = tmps;
    }


    Temp findElement(String a) {

        for (Temp temp : temps) {

            if (temp.tempname.equals(a)) {
                return temp;
            }
        }
        return null;
    }

    void print() {
        System.out.println("Scope: " + scope);
        for (Temp temp : temps) temp.print();
    }
}
