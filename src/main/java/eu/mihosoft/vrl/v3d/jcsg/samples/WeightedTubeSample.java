/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.v3d.jcsg.samples;

import eu.mihosoft.vrl.v3d.jcsg.CSG;
import eu.mihosoft.vrl.v3d.jcsg.Cylinder;
import eu.mihosoft.vrl.v3d.jcsg.FileUtil;
import eu.mihosoft.vrl.v3d.jcsg.Transform;
import eu.mihosoft.vrl.v3d.jcsg.UnityModifier;
import eu.mihosoft.vrl.v3d.jcsg.Vector3d;
import eu.mihosoft.vrl.v3d.jcsg.WeightFunction;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class WeightedTubeSample {

    public CSG toCSG() {
        
        

        WeightFunction weight = new WeightFunction() {

            @Override
            public double eval(Vector3d v, CSG csg) {
                double w = Math.max(1,1.0/(v.z*0.1+0.1));
                
                return w;
            }
        };
        
//        CSG.setDefaultOptType(CSG.OptType.POLYGON_BOUND);

        CSG protoOuter = new Cylinder(1, 1, 16).toCSG();
        CSG protoInner = new Cylinder(0.5, 1, 16).toCSG();

        CSG outer = protoOuter;
        CSG inner = protoInner;

        for (int i = 0; i < 50; i++) {
            outer = outer.union(protoOuter.transformed(Transform.unity().translateZ(i / 5.0)));
            inner = inner.union(protoInner.transformed(Transform.unity().translateZ(i / 5.0)));
        }
        
        Transform scale = Transform.unity().scale(2, 2, 1);
        Transform scaleInner = Transform.unity().scale(1.5, 1.5, 1);

        inner = inner.weighted(weight).transformed(scaleInner).weighted(new UnityModifier());

        return outer.weighted(weight).
                transformed(scale).difference(inner);
    }

    public static void main(String[] args) throws IOException {

        FileUtil.write(new File("weighted-tube.stl"), new WeightedTubeSample().toCSG().toStlString());

        new WeightedTubeSample().toCSG().toObj().toFiles(new File("weighted-tube.obj"));

    }

}