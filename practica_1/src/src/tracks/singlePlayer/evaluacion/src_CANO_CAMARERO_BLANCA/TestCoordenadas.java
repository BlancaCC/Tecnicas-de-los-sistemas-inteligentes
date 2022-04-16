package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class TestCoordenadas{
    public static void main(String[] args) {
        Coordenadas a = new Coordenadas(1, 2);
        Coordenadas b = new Coordenadas(1, 1);
        Coordenadas c = new Coordenadas(1, 1);
        Coordenadas d = new Coordenadas(1, 3);


        System.out.println(c.compareTo(b)); // iguales 0
        System.out.println(c.compareTo(a)); // c menos que a -1
        System.out.println(a.compareTo(b)); // a mayor que b 1

        Set<Coordenadas> s = new HashSet<>();
        s.add(a);
        s.add(c);
        System.out.println(s);
        System.out.println(c.equals(b)); // true
        System.out.println(s.contains(b)); // true

        Hashtable<Coordenadas, Integer> heuristica = new Hashtable<>();
        heuristica.put(a, 1);
        heuristica.put(c,2);
        heuristica.put(c, 3);
        System.out.println(heuristica); // {(1,2)=1, (1,1)=3}
        System.out.println(heuristica.get(b)); // 3
        System.out.println(heuristica.containsKey(d)); // false
        System.out.println(heuristica.containsKey(b)); // true <--- Da error


    }

}
