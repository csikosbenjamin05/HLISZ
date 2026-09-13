package base3D.shapeLibrary;

import base3D.ENV;
import base3D.resources.ResourceManager;
import base3D.definitions.ResourceUser;
import processing.core.PConstants;
import processing.core.PShape;

public class SphereShapeGenerator extends ShapeGenerator {
    public boolean drawEdge;
    public boolean drawFace;
    public int faceColor;
    public int edgeColor;

    public int radius;
    public int sphereDetail;

    ////////////////////////////
    // Constructors ->

    public SphereShapeGenerator(ResourceUser parent) {
        super(parent, false);
        faceColor = ENV.p.color(100, 0, 0);
        edgeColor = ENV.p.color(0, 255, 0);
        drawEdge = true;
        drawFace = true;
        sphereDetail = 30;

        radius = 100;
    }

    public SphereShapeGenerator() {
        this((ResourceUser) null);
    }


    public SphereShapeGenerator(ResourceUser parent, int radius) {
        this(parent);
        this.radius = radius;
    }

    public SphereShapeGenerator(int radius) {
        this(null, radius);
    }

    public SphereShapeGenerator(ResourceUser parent, int radius, String texture) {
        this(parent, radius);
        drawEdge = false;
        faceColor = ENV.p.color(255);

        texture_name = texture;
        paintWithTexture = true;
    }

    public SphereShapeGenerator(int radius, String texture) {
        this(null, radius, texture);
    }

    // copy constructor
    public SphereShapeGenerator(SphereShapeGenerator sphereGen) {
        this(sphereGen.getParent());
        set(sphereGen);
    }

    public void set(SphereShapeGenerator sphereGen) {
        set(((ShapeGenerator) sphereGen));

        this.faceColor = sphereGen.faceColor;
        this.edgeColor = sphereGen.edgeColor;
        this.drawEdge = sphereGen.drawEdge;
        this.drawFace = sphereGen.drawFace;
        this.radius = sphereGen.radius;
    }

    ////////////////////////////
    // Generators ->

    @Override
    PShape generateTexturedShape() {
        PShape sh = generateTexturelessShape();

        sh.setTexture(ResourceManager.getTexture(texture_name, parent::reGenerateElement).get());

        return sh;
    }

    @Override
    PShape generateTexturelessShape() {
        ENV.p.sphereDetail(sphereDetail);
        PShape sh = ENV.p.createShape(PConstants.SPHERE, radius);

        sh.setFill(faceColor);
        sh.setStroke(edgeColor);

        sh.setFill(drawFace);
        sh.setStroke(drawEdge);

        return sh;
    }
    public void setShapeHolderOffset(ShapeHolder sh) {
        sh.setOffSet(0);
    }
}

/*
//===========================================================================
// IMPORTS:
import peasy.PeasyCam;



//===========================================================================
// GLOBAL VARIABLES:

PeasyCam cam;

int r=180;
int sr=3;
int total=30;

PVector[][] vv=new PVector[total][total];

//===========================================================================
// PROCESSING DEFAULT FUNCTIONS:

void setup() {
size(1080, 800, P3D);
  textAlign(CENTER, CENTER);
  rectMode(CENTER);

  noStroke();

  smooth(8);
  cam = new PeasyCam(this, 400);



  for (int e=0; e<total; e++) {
    float lon = map(e, 0, total, 0, TWO_PI);

    for (int f=0; f<total; f++) {
      float lat = map(f, 0, total, 0, PI);

      PVector p = new PVector( r * sin(lat) * cos(lon),
        r * sin(lat) * sin (lon),
        r * cos(lat));
      vv[e][f]=p;
    }
  }
}

void draw() {
  background(0);
  //noStroke();
  stroke(0);

  for (float e=0; e<total/1.0; e++) {
    beginShape(TRIANGLE_STRIP);
    for (float f=0; f<total; f++) {
      fill(lerp(0, 255, e*1.0/total), lerp(0, 255, f*1.0/total), 25);

      if (f-1>0) {
        PVector p= vv[int(e)][int(f-1)%total];
        vertex(p.x, p.y, p.z);
      }

      //if (e+1<total) {
      PVector q= vv[int(e+1)%total][int(f-0)%total];
      vertex(q.x, q.y, q.z);
      //}
    }
    vertex(0, 0, -180);
    endShape();
  }
}
 */