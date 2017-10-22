package wrestling.model.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import wrestling.model.Worker;
import wrestling.model.controller.GameController;
import wrestling.model.utility.ModelUtilityFunctions;

/**
 *
 * allows the creation of an arbitrary amount of random workers
 */
public class WorkerFactory {

    private final List<String> FIRST_NAMES = Arrays.asList(("JAMES,JOHN,ROBERT,MICHAEL,WILLIAM,DAVID,RICHARD,CHARLES,JOSEPH,THOMAS,CHRISTOPHER,DANIEL,PAUL,MARK,DONALD,GEORGE,KENNETH,STEVEN,EDWARD,BRIAN,RONALD,ANTHONY,KEVIN,JASON,MATTHEW,GARY,TIMOTHY,JOSE,LARRY,JEFFREY,FRANK,SCOTT,ERIC,STEPHEN,ANDREW,RAYMOND,GREGORY,JOSHUA,JERRY,DENNIS,WALTER,PATRICK,PETER,HAROLD,DOUGLAS,HENRY,CARL,ARTHUR,RYAN,ROGER,JOE,JUAN,JACK,ALBERT,JONATHAN,JUSTIN,TERRY,GERALD,KEITH,SAMUEL,WILLIE,RALPH,LAWRENCE,NICHOLAS,ROY,BENJAMIN,BRUCE,BRANDON,ADAM,HARRY,FRED,WAYNE,BILLY,STEVE,LOUIS,JEREMY,AARON,RANDY,HOWARD,EUGENE,CARLOS,RUSSELL,BOBBY,VICTOR,MARTIN,ERNEST,PHILLIP,TODD,JESSE,CRAIG,ALAN,SHAWN,CLARENCE,SEAN,PHILIP,CHRIS,JOHNNY,EARL,JIMMY,ANTONIO,DANNY,BRYAN,TONY,LUIS,MIKE,STANLEY,LEONARD,NATHAN,DALE,MANUEL,RODNEY,CURTIS,NORMAN,ALLEN,MARVIN,VINCENT,GLENN,JEFFERY,TRAVIS,JEFF,CHAD,JACOB,LEE,MELVIN,ALFRED,KYLE,FRANCIS,BRADLEY,JESUS,HERBERT,FREDERICK,RAY,JOEL,EDWIN,DON,EDDIE,RICKY,TROY,RANDALL,BARRY,ALEXANDER,BERNARD,MARIO,LEROY,FRANCISCO,MARCUS,MICHEAL,THEODORE,CLIFFORD,MIGUEL,OSCAR,JAY,JIM,TOM,CALVIN,ALEX,JON,RONNIE,BILL,LLOYD,TOMMY,LEON,DEREK,WARREN,DARRELL,JEROME,FLOYD,LEO,ALVIN,TIM,WESLEY,GORDON,DEAN,GREG,JORGE,DUSTIN,PEDRO,DERRICK,DAN,LEWIS,ZACHARY,COREY,HERMAN,MAURICE,VERNON,ROBERTO,CLYDE,GLEN,HECTOR,SHANE,RICARDO,SAM,RICK,LESTER,BRENT,RAMON,CHARLIE,TYLER,GILBERT,GENE,MARC,REGINALD,RUBEN,BRETT,ANGEL,NATHANIEL,RAFAEL,LESLIE,EDGAR,MILTON,RAUL,BEN,CHESTER,CECIL,DUANE,FRANKLIN,ANDRE,ELMER,BRAD,GABRIEL,RON,MITCHELL,ROLAND,ARNOLD,HARVEY,JARED,ADRIAN,KARL,CORY,CLAUDE,ERIK,DARRYL,JAMIE,NEIL,JESSIE,CHRISTIAN,JAVIER,FERNANDO,CLINTON,TED,MATHEW,TYRONE,DARREN,LONNIE,LANCE,CODY,JULIO,KELLY,KURT,ALLAN,NELSON,GUY,CLAYTON,HUGH,MAX,DWAYNE,DWIGHT,ARMANDO,FELIX,JIMMIE,EVERETT,JORDAN,IAN,WALLACE,KEN,BOB,JAIME,CASEY,ALFREDO,ALBERTO,DAVE,IVAN,JOHNNIE,SIDNEY,BYRON,JULIAN,ISAAC,MORRIS,CLIFTON,WILLARD,DARYL,ROSS,VIRGIL,ANDY,MARSHALL,SALVADOR,PERRY,KIRK,SERGIO,MARION,TRACY,SETH,KENT,TERRANCE,RENE,EDUARDO,TERRENCE,ENRIQUE,FREDDIE,WADE").split("\\s*,\\s*"));
    private final List<String> LAST_NAMES = Arrays.asList(("Smith,Jones,Taylor,Williams,Brown,Davies,Evans,Wilson,Thomas,Roberts,Johnson,Lewis,Walker,Robinson,Wood,Thompson,White,Watson,Jackson,Wright,Green,Harris,Cooper,King,Lee,Martin,Clarke,James,Morgan,Hughes,Edwards,Hill,Moore,Clark,Harrison,Scott,Young,Morris,Hall,Ward,Turner,Carter,Phillips,Mitchell,Patel,Adams,Campbell,Anderson,Allen,Cook,Bailey,Parker,Miller,Davis,Murphy,Price,Bell,Baker,Griffiths,Kelly,Simpson,Marshall,Collins,Bennett,Cox,Richardson,Fox,Gray,Rose,Chapman,Hunt,Robertson,Shaw,Reynolds,Lloyd,Ellis,Richards,Russell,Wilkinson,Khan,Graham,Stewart,Reid,Murray,Powell,Palmer,Holmes,Rogers,Stevens,Walsh,Hunter,Thomson,Matthews,Ross,Owen,Mason,Knight,Kennedy,Butler,Saunders,Cole,Pearce,Dean,Foster,Harvey,Hudson,Gibson,Mills,Berry,Barnes,Pearson,Kaur,Booth,Dixon,Grant,Gordon,Lane,Harper,Ali,Hart,Mcdonald,Brooks,Ryan,Carr,Macdonald,Hamilton,Johnston,West,Gill,Dawson,Armstrong,Gardner,Stone,Andrews,Williamson,Barker,George,Fisher,Cunningham,Watts,Webb,Lawrence,Bradley,Jenkins,Wells,Chambers,Spencer,Poole,Atkinson,Lawson,Lawson,Day,Woods,Rees,Fraser,Black,Fletcher,Hussain,Willis,Marsh,Ahmed,Doyle,Lowe,Burns,Hopkins,Nicholson,Parry,Newman,Jordan,Henderson,Howard,Barrett,Burton,Riley,Porter,Byrne,Houghton,John,Perry,Baxter,Ball,Mccarthy,Elliott,Burke,Gallagher,Duncan,Cooke,Austin,Read,Wallace,Hawkins,Hayes,Francis,Sutton,Davidson,Sharp,Holland,Moss,May,Bates,Morrison,Bob,Oliver,Kemp,Page,Arnold,Shah,Stevenson,Ford,Potter,Flynn,Warren,Kent,Alexander,Field,Freeman,Begum,Rhodes,Oneill,Middleton,Payne,Stephenson,Pritchard,Gregory,Bond,Webster,Dunn,Donnelly,Lucas,Long,Jarvis,Cross,Stephens,Reed,Coleman,Nicholls,Bull,Bartlett,Obrien,Curtis,Bird,Patterson,Tucker,Bryant,Lynch,Mackenzie,Ferguson,Cameron,Lopez,Haynes").split("\\s*,\\s*"));

    public List createWorkers(int numberOfWorkers) {

        ArrayList<Worker> workers = new ArrayList<>();

        for (int i = 0; i < numberOfWorkers; i++) {
            workers.add(randomWorker());

        }

        return workers;
    }

    public List createRoster(int rosterSize, int rosterLevel) {

        ArrayList<Worker> roster = new ArrayList<>();

        for (int i = 0; i < rosterSize; i++) {
            roster.add(randomWorker(rosterLevel));
        }
        return roster;
    }

    //to generate a random worker at a given popularity level
    public Worker randomWorker(int level) {

        if (level < 1) {
            level = 1;
        } else if (level > 5) {
            level = 5;
        }

        Worker worker = newWorker();

        //set the popularity to be proportionate to the level requested
        worker.setPopularity((level * 20) + ModelUtilityFunctions.randRange(-10, 10));

        //prevent too many maxed out workers
        if (worker.getPopularity() > 100) {
            worker.setPopularity(100 + ModelUtilityFunctions.randRange(-10, 0));
        }

        worker.setFlying(ModelUtilityFunctions.randRange(0, 100));
        worker.setCharisma(ModelUtilityFunctions.randRange(0, 100));
        worker.setBehaviour(ModelUtilityFunctions.randRange(0, 100));
        worker.setWrestling(ModelUtilityFunctions.randRange(0, 100));
        worker.setStriking(ModelUtilityFunctions.randRange(0, 100));

        worker.setManager(false);
        worker.setMainRoster(true);
        worker.setFullTime(true);

        setRandomName(worker);

        return worker;
    }

    public Worker randomWorker() {
        Worker worker = newWorker();

        worker.setPopularity(ModelUtilityFunctions.randRange(0, 100));
        worker.setFlying(ModelUtilityFunctions.randRange(0, 100));
        worker.setCharisma(ModelUtilityFunctions.randRange(0, 100));
        worker.setBehaviour(ModelUtilityFunctions.randRange(0, 100));
        worker.setWrestling(ModelUtilityFunctions.randRange(0, 100));
        worker.setStriking(ModelUtilityFunctions.randRange(0, 100));

        worker.setManager(false);
        worker.setMainRoster(true);
        worker.setFullTime(true);

        setRandomName(worker);

        return worker;
    }

    private void setRandomName(Worker worker) {
        Random random = new Random();
        String nameString;

        int index = random.nextInt(FIRST_NAMES.size());
        String firstName = FIRST_NAMES.get(index);
        firstName = firstName.toLowerCase();
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);

        index = random.nextInt(LAST_NAMES.size());
        String lastName = LAST_NAMES.get(index);

        nameString = firstName + " " + lastName;

        worker.setName(nameString);
        worker.setShortName(lastName);
    }

    private Worker newWorker() {
        Worker worker = new Worker();
        return worker;
    }
}
