package app.services;

public class CarportSvg {
    private int width;
    private int length;
    private Svg carportSvg;
    private String rectStyle = "stroke:black;fill: white";
    private Calculator calculator;

    public CarportSvg(int width, int length) {
        this.width = width;
        this.length = length;
        calculator = new Calculator(width, length);
        carportSvg = new Svg(0,0,"100%", "0 0 855 690");
        carportSvg.addRectangle(0,0 ,this.length, this.width,rectStyle );

        addBeams();
        addRafters();
    }

    private void addBeams (){
        carportSvg.addRectangle(0,0+35,5,this.width, rectStyle);
        carportSvg.addRectangle(0, length -40,5,this.width, rectStyle);
    }

    private void addRafters(){
        int calculatedRafters = calculator.calcRaftersQuantity();

        for(int i = 0; i<=this.width; i += width/calculatedRafters){
        carportSvg.addRectangle(i,0,this.length,5,rectStyle);}

    }

    private void addPosts(){

        carportSvg.addRectangle(100,35,10,10, rectStyle);
        carportSvg.addRectangle(425,35,10,10, rectStyle);
        carportSvg.addRectangle(750,35,10,10, rectStyle);

    }

    @Override
    public String toString() {
        return carportSvg.toString();
    }
}
