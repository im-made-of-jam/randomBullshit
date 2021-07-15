import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class ConsoleRender{
    public static void main(String[] args) throws IOException, InterruptedException{
        
    }
}

/**
 * Lots of these for a square-ish frame. All 'pixels' false / blank when created.
 * Major component of {@code Frame}.
 */
class Line{
    private long length;

    protected List<Boolean> chars = new ArrayList<Boolean>(); // bools because all the fancy stuff happens in Frame.pushToConsole()

    Line(long length){ // this can definitely be made better but i cant be arsed, and this works
        this.length = length;

        for(long i = 0; i < this.length; ++i){
            chars.add(false);
        }
    }

    /**
     * if (blank) the line will be empty, else it will be whatever the char is set to be
     */
    protected void fillLine(boolean blank){
        chars.clear();

        for(long i = 0; i < length; ++i){
            chars.add(!blank); // gotta be inverted because im dumb and didnt make it otherwise but whatever this is going to c++ ill fix it there
        }
    }

    protected void changeChar(boolean blank, long position){
        chars.set((int)position, blank); // dont like casting but hey it means that the thingmy can take a long as an argument and thats fine by me
    }
}

/**
 * need this to draw weeb shit on. also can put stuff on console because im nice
 * Major component of {@code Renderer}.
 * @param explain_themselves
 * @param numberHorizontalChars
 * @param numberVerticalChars
 */
class Frame{
    protected List<Line> lineList = new ArrayList<Line>(); // this is it. this is the frame. this is where the weeb-eyness gets kept

    protected char displayChar;     // this is the char that gets used to display the weeb stuff on screen. no gradient because gradient bad
    protected char emptyChar = ' '; // this is the char for everything else, not the weeb shit thank fuck

    Frame(long numberHorizontalChars, long numberVerticalChars, char charToDisplay){
        for(long i = 0; i < numberVerticalChars; ++i){
            lineList.add(new Line(numberHorizontalChars));
        }

        displayChar = charToDisplay;
    }

    Frame(long numberHorizontalChars, long numberVerticalChars){
        for(long i = 0; i < numberVerticalChars; ++i){
            lineList.add(new Line(numberHorizontalChars));
        }

        displayChar = '#';
    }

    Frame(char charToDisplay){ // defaults to 80 by 25 because theyre nice numbers
        for(long i = 0; i < 25; ++i){
            lineList.add(new Line(80));
        }

        displayChar = charToDisplay;
    }

    Frame(){ // defaults to 80 by 25 because theyre nice numbers
        for(long i = 0; i < 25; ++i){
            lineList.add(new Line(80));
        }

        displayChar = '#';
    }

    /**
     * put whatever weeb shit is stored in this frame onto the console, hopefully in one piece
     * @param clearScreen whether or not the previous frame gets nuked
     * @throws ALotOfShit because of the screen clearing
    */
    protected void pushToConsole(boolean clearScreen) throws IOException, InterruptedException{
        if(clearScreen){
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); // this fuckery clears the console
        }

        pushToConsole();
    }

    private void pushToConsole(){
        List<String> pushThis = new ArrayList<String>(); // this is the final step before the weeb shit appears in front of your eyes. it gets put here then put into existance
        String workingpls; // this is for working on a line at a time, name is not important

        for(Line line : lineList){
            workingpls = ""; // blanket
            for(boolean bool : line.chars){
                if(bool){
                    workingpls = workingpls + displayChar; // bear with me . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . look here! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! this is where the char that gets put on the console is kept ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !
                } else {
                    workingpls = workingpls + emptyChar;
                }
            }
            pushThis.add(workingpls);
        }

        for(String hahahaJonathanYouAreBangingMyConsole : pushThis){ // once everything has finally been completed
            System.out.println(hahahaJonathanYouAreBangingMyConsole); // ha ha jonathan your weeb shit is banging my console
        }
    }

    /**
     * change an entire line to something
     * @param lineNumber
     * @param blank if true, line will be blanked and empty
     */
    protected void changeLine(long lineNumber, boolean blank){ // another int because of Line.set()
        Line workingLine = lineList.get((int)lineNumber); // see above comment in Line.changeChar()
        workingLine.fillLine(blank);
        lineList.set((int)lineNumber, workingLine);
    }

    /**
     * changes one char ('pixel')
     * @param lineNumber what line the char is on
     * @param charPosition where along that line the char is
     * @param blank if true, the char will be a space, else whatever is set
     */
    protected void changeChar(long lineNumber, long charPosition, boolean blank){
        Line workingLine = lineList.get((int)lineNumber);
        workingLine.changeChar(blank, charPosition);
        lineList.set((int)lineNumber, workingLine);
    }

    /**
     * should ideally not be used, but can be for fun
     * @param toThis your empty space will be filled by whatever you put into here
     */
    protected void changeEmptyChar(char toThis){
        emptyChar = toThis;
    }

    /**
     * fills the screen
     * @param blank if(true) screen will be blank, otherwise filled
     */
    protected void fillScreen(boolean blank){
        for(Line line : lineList){
            line.fillLine(blank);
        }
    }

    protected void copyListToSelf(Frame fromHere){
        this.lineList = fromHere.lineList;
    }
}

/**
 * Renders to the console with a <i>(kind of)</i> double buffer.
 */
class Renderer{
    /** Frame that methods manupulate. Separate from display frame for a <i>kind of</i> double buffer. */
    private Frame workingFrame;

    /** Frame that is displayed. Methods do not manipulate this. */
    private Frame displayFrame;

    protected double lineStepSize = 0.5; // step size to be used while drawing lines. details in drawLine()

    Renderer(long width, long height, char displayChar){
        workingFrame = new Frame(width, height, displayChar);
        displayFrame = new Frame(width, height, displayChar);
    }

    Renderer(long width, long height){
        workingFrame = new Frame(width, height, '#');
        displayFrame = new Frame(width, height, '#');
    }

    Renderer(char displayChar){
        workingFrame = new Frame('#');
        displayFrame = new Frame('#');
    }

    Renderer(){
        workingFrame = new Frame('#');
        displayFrame = new Frame('#');
    }

    /** Swaps the chars used for stuff being there and stuff not being there */
    protected void invertDrawingChars(){
        char tempEmpty = workingFrame.emptyChar;
        char tempDisplay = workingFrame.displayChar;

        workingFrame.emptyChar = tempDisplay;
        displayFrame.emptyChar = tempDisplay;

        workingFrame.displayChar = tempEmpty;
        displayFrame.displayChar = tempEmpty;
    }

    /**
     * should ideally never be used, but is here just in case needed
     * @param toThis the char that empty space will be filled with
     */
    protected void changeEmptyChar(char toThis){
        workingFrame.emptyChar = toThis;
        displayFrame.emptyChar = toThis;
    }

    /**
     * this should be used when you want to change the char used to display your shapes
     * @param toThis the char used to display stuff
     */
    protected void changeDisplayChar(char toThis){
        workingFrame.displayChar = toThis;
        displayFrame.displayChar = toThis;
    }

    /** Change the frame size.
     * @param width the width of the new frame
     * @param height the height of the new frame
     */
    protected void changeSize(long width, long height){ // basically re-does the construction of the object
        // this is so that the chars remain the same even when the screen size changes
        char wEmpty = workingFrame.emptyChar;
        char dEmpty = displayFrame.emptyChar;
        char wDisplay = workingFrame.displayChar;
        char dDisplay = displayFrame.displayChar;

        workingFrame = new Frame(width, height);
        displayFrame = new Frame(width, height);

        workingFrame.emptyChar = wEmpty;
        workingFrame.displayChar = wDisplay;

        displayFrame.emptyChar = dEmpty;
        displayFrame.displayChar = dDisplay;
    }

    /** Change a single char
     * @param xCoordinate of the pixel to be changed
     * @param yCoordinate of the pixel to be changed
     * @param blank whether or not the pixel is blank
     */
    protected void changeChar(long xCoordinate, long yCoordinate, boolean blank){
        workingFrame.changeChar(xCoordinate, yCoordinate, blank);
    }

    /** Change a single char
     * @param xCoordinate of the pixel to be changed
     * @param yCoordinate of the pixel to be changed
     */
    protected void changeChar(long xCoordinate, long yCoordinate){
        workingFrame.changeChar(xCoordinate, yCoordinate, false);
    }

    /** Draws a line between two points, {@code start} and {@code end}. Each point has two arguments, one for each coordinate. Uses {@code this.lineStepSize} as a step between drawing points. Larger values than 0.5 not recommended.
     * @param startX X coordinate of the start point
     * @param startY Y coordinate of the start point
     * @param endX X coordinate of the end point
     * @param endY Y coordinate of the end point
     * @param blank whether or not the line is blank
     */
    protected void drawLine(long startX, long startY, long endX, long endY, boolean blank){
        double deltaX = endX - startX;
        double deltaY = endY - startY;

        double theta = Math.atan2(deltaY, deltaX); // feels like these two are the wrong way around, but apparently not

        double stepX = Math.cos(theta) * lineStepSize; // these two need to be calculated so that the steps can be angled properly
        double stepY = Math.sin(theta) * lineStepSize;

        double currXPos = startX;
        double currYPos = startY;

        long noSteps = Math.round(deltaX / stepX);

        for(long i = 0; i < noSteps; ++i){
            this.changeChar(
                            Math.round(currXPos), // the X coordinate of the pixel to be changed. these are rounded so that you dont end up with needing to fill in half of a pixel
                            Math.round(currYPos), // the Y coordinate of the pixel to be changed.
                            blank);

            currXPos += stepX;
            currYPos += stepY;
        }
    }

    /** Draws a line between two points, {@code start} and {@code end}. Each point has two arguments, one for each coordinate.
     * @param startX X coordinate of the start point
     * @param startY Y coordinate of the start point
     * @param endX X coordinate of the end point
     * @param endY Y coordinate of the end point
     */
    protected void drawLine(long startX, long startY, long endX, long endY){
        drawLine(startX,  startY, endX, endY, false);
    }

    protected void drawRectangle(long startX, long startY, long endX, long endY, boolean blank){
        long deltaX = endX - startX;
        long deltaY = endY - startY;

        for(long i = 0; i < deltaY; ++i){
            for(long j = 0; j < deltaX; ++j){
                Line a = workingFrame.lineList.get((int)(i + startY));

                a.changeChar(!blank, j + startX);

                workingFrame.lineList.set((int)(i + startY), a);
            }
        }
    }

    protected void drawRectangle(long startX, long startY, long endX, long endY){
        drawRectangle(startX, startY, endX, endY, false);
    }

    /** Fill the screen 
     * @param blank if(true), screen will be blanked, else filled in completely
    */
    protected void fillScreen(boolean blank){
        workingFrame.fillScreen(blank);
    }

    /** copies workingFrame into displayFrame, then blanks workingFrame. then displays displayFrame */
    protected void pushFrameToConsole() throws InterruptedException, IOException{
        displayFrame.copyListToSelf(workingFrame);

        displayFrame.pushToConsole(true);

        workingFrame.fillScreen(true);
    }
}