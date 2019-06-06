package wrestling.model.manager;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import wrestling.model.AngleParams;
import wrestling.model.Match;
import wrestling.model.MatchTitle;
import wrestling.model.SegmentItem;
import wrestling.model.SegmentWorker;
import wrestling.model.Title;
import wrestling.model.interfaces.Segment;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.StableView;
import wrestling.model.modelView.TagTeamView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.AngleType;
import wrestling.model.segmentEnum.JoinTeamType;
import wrestling.model.segmentEnum.MatchFinish;
import wrestling.model.segmentEnum.MatchRule;
import wrestling.model.segmentEnum.ResponseType;
import wrestling.model.segmentEnum.SegmentType;
import wrestling.model.segmentEnum.TeamType;
import wrestling.model.utility.ModelUtils;
import static wrestling.model.utility.ModelUtils.slashShortNames;
import wrestling.model.utility.SegmentStringUtils;
import wrestling.view.utility.ViewUtils;

public class SegmentManager implements Serializable {

    private final List<Segment> segments;
    private final List<SegmentWorker> segmentWorkers;
    private final List<MatchTitle> matchTitles;
    private final List<SegmentView> segmentViews;
    private final DateManager dateManager;
    private final TagTeamManager tagTeamManager;
    private final StableManager stableManager;

    public SegmentManager(DateManager dateManager, TagTeamManager tagTeamManager, StableManager stableManager) {
        segments = new ArrayList<>();
        segmentWorkers = new ArrayList<>();
        matchTitles = new ArrayList<>();
        segmentViews = new ArrayList<>();
        this.dateManager = dateManager;
        this.tagTeamManager = tagTeamManager;
        this.stableManager = stableManager;
    }

    public void addSegmentWorker(SegmentWorker segmentWorker) {
        segmentWorkers.add(segmentWorker);
    }

    public void addMatchTitle(MatchTitle matchTitle) {
        matchTitles.add(matchTitle);
    }

    public void addSegment(Segment segment) {
        segments.add(segment);
    }

    public void addSegmentView(SegmentView segmentView) {
        segmentViews.add(segmentView);
    }

    public Title getTitle(Match match) {
        Title title = null;
        for (MatchTitle matchTitle : matchTitles) {
            if (matchTitle.getMatch().equals(match)) {
                title = matchTitle.getTitle();
            }
        }
        return title;
    }

    public List<WorkerView> getWinners(Match match) {
        List<WorkerView> winners = new ArrayList<>();
        for (SegmentWorker matchWorker : segmentWorkers) {
            if (matchWorker.getSegment().equals(match)
                    && matchWorker.getTeam() == 0) {
                winners.add(matchWorker.getWorker());
            }
        }
        return winners;
    }

    public List<WorkerView> getWorkers(Segment segment) {
        List<WorkerView> workers = new ArrayList<>();
        for (SegmentWorker matchWorker : segmentWorkers) {
            if (matchWorker.getSegment().equals(segment)) {
                workers.add(matchWorker.getWorker());
            }
        }
        return workers;
    }

    private List<SegmentWorker> getMatchWorkers(Match match) {
        List<SegmentWorker> getMatchWorkers = new ArrayList<>();
        segmentWorkers.stream().filter((matchWorker) -> (matchWorker.getSegment().equals(match))).forEach((matchWorker) -> {
            getMatchWorkers.add(matchWorker);
        });
        return getMatchWorkers;
    }

    public String getMatchStringForMonths(WorkerView worker, int months) {

        StringBuilder sb = new StringBuilder();

        for (SegmentView segmentView : segmentViews) {
            if (segmentView.getWorkers().contains(worker)
                    && segmentView.getDate().isAfter(dateManager.today().minusMonths(months))) {
                sb.append(getSegmentString(segmentView, true));
                sb.append("\n");
            }
        }

        return sb.length() > 0
                ? sb.toString() : "No recent matches";

    }

    public List<SegmentView> getTopMatches(LocalDate localDate, ChronoUnit unit, int units, int totalMatches) {

        LocalDate earliestDate = localDate;
        switch (unit) {
            case FOREVER:
                earliestDate = LocalDate.MIN;
                break;
            case WEEKS:
                for (int i = 0; i < units; i++) {
                    earliestDate = localDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
                }
                break;
            case YEARS:
                earliestDate = localDate.withDayOfYear(1);
                if (units > 1) {
                    earliestDate = earliestDate.minusYears(units);
                }
                break;
            case MONTHS:
                earliestDate = localDate.withDayOfMonth(1);
                if (units > 1) {
                    earliestDate = earliestDate.minusMonths(units);
                }
                break;
        }
        List<SegmentView> weekMatches = new ArrayList<>();
        for (SegmentView segmentView : segmentViews) {
            if (segmentView.getDate().isAfter(earliestDate) && segmentView.getSegmentType().equals(SegmentType.MATCH)) {
                weekMatches.add(segmentView);
            }
        }
        Collections.sort(weekMatches, (SegmentView sv1, SegmentView sv2)
                -> sv2.getSegment().getWorkRating() - sv1.getSegment().getWorkRating());

        int actualTotal = totalMatches <= weekMatches.size()
                ? totalMatches
                : weekMatches.size();

        return new ArrayList<>(weekMatches.subList(0, actualTotal));
    }

    public String getSegmentTitle(SegmentView segmentView) {
        if (segmentView.getSegmentType().equals(SegmentType.MATCH)) {
            return SegmentStringUtils.getMatchTitle(segmentView);
        }
        return getAngleTitle(segmentView);
    }

    private String getAngleTitle(SegmentView segmentView) {
        return ((AngleParams) segmentView.getSegment().getSegmentParams()).getAngleType().description();
    }

    public String getIsolatedSegmentString(SegmentView segmentView) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(segmentView.getEventView().getVerboseEventTitle());
        stringBuilder.append("\n");
        stringBuilder.append(getSegmentString(segmentView));
        stringBuilder.append("\n");
        stringBuilder.append(segmentView.getSegmentType().equals(SegmentType.MATCH)
                ? ViewUtils.intToStars(segmentView.getSegment().getWorkRating())
                : "Rating: " + segmentView.getSegment().getWorkRating() + "%");

        return stringBuilder.toString();
    }

    public String getSegmentString(SegmentView segmentView) {
        return getSegmentString(segmentView, false);
    }

    public String getSegmentString(SegmentView segmentView, boolean verbose) {
        String segmentString = segmentView.getSegmentType().equals(SegmentType.MATCH)
                ? getMatchString(segmentView, verbose)
                : getAngleString(segmentView);
        if (verbose) {
            segmentString += " @ " + segmentView.getEventView().toString();
        }
        return segmentString;
    }

    public String getAngleString(SegmentView segmentView) {
        AngleType angleType = ((AngleParams) segmentView.getSegment().getSegmentParams()).getAngleType();
        List<SegmentTeam> mainTeam = segmentView.getTeams(angleType.mainTeamType());
        String mainTeamString;
        String pluralString;
        if (mainTeam.isEmpty()) {
            mainTeamString = "?";
            pluralString = "";
        } else {
            mainTeamString = generateTeamName(mainTeam.get(0).getWorkers(), true, mainTeam.get(0).getType());
            pluralString = mainTeam.get(0).getWorkers().size() > 1 ? "" : "s";
        }
        List<String> andTeamNames = new ArrayList<>();
        for (SegmentTeam tesm : segmentView.getTeams(angleType.addTeamType())) {
            andTeamNames.add(generateTeamName(tesm.getWorkers()));
        }

        String string = String.format(angleType.resultString(),
                mainTeamString,
                pluralString,
                ModelUtils.joinGrammatically(andTeamNames));

        if (angleType.equals(AngleType.PROMO) && segmentView.getTeams(TeamType.PROMO_TARGET).isEmpty()) {
            string = string.split("targeting")[0];
            string = string.replace(" targeting", "");
        }

        if (angleType.equals(AngleType.OFFER)) {
            string += SegmentStringUtils.getOfferString(segmentView);
        } else if (angleType.equals(AngleType.CHALLENGE)) {
            string += SegmentStringUtils.getChallengeString(segmentView);
        }

        return string;

    }

    public String generateTeamName(List<? extends SegmentItem> segmentItems, TeamType teamType) {
        return generateTeamName(segmentItems, false, teamType);
    }

    public String generateTeamName(List<? extends SegmentItem> segmentItems) {
        return generateTeamName(segmentItems, false, TeamType.DEFAULT);
    }

    public String generateTeamName(List<? extends SegmentItem> segmentItems, boolean verbose) {
        return generateTeamName(segmentItems, verbose, TeamType.DEFAULT);
    }

    public String generateTeamName(List<? extends SegmentItem> segmentItems, boolean verbose, TeamType teamType) {
        if (!segmentItems.isEmpty()) {
            if (segmentItems.size() == 2) {
                String tagTeam = getTagTeamName(segmentItems);
                if (!tagTeam.isEmpty()) {
                    return tagTeam;
                }
            } else if (segmentItems.size() > 1 && !TeamType.OFFEREE.equals(teamType) && !TeamType.OFFERER.equals(teamType)) {
                for (StableView stable : stableManager.getStables()) {
                    if (stable.getWorkers().containsAll(segmentItems)) {
                        return stable.getName();
                    }
                }
            }
            return verbose ? ModelUtils.slashNames(segmentItems) : slashShortNames(segmentItems);
        } else {
            return "(Empty Team)";
        }
    }

    public String getTagTeamName(List<? extends SegmentItem> segmentItems) {
        for (TagTeamView tagTeamView : tagTeamManager.getTagTeamViews()) {
            if (tagTeamView.getSegmentItems().containsAll(segmentItems)) {
                return tagTeamView.getTagTeam().getName();
            }
        }
        return String.format("");
    }

    public String getVsMatchString(SegmentView segmentView) {
        List<SegmentTeam> teams = segmentView.getTeams();
        int teamsSize = segmentView.getMatchParticipantTeams().size();
        String matchString = "";

        if (teamsSize > 1) {

            for (int t = 0; t < teamsSize; t++) {
                List<WorkerView> team = teams.get(t).getWorkers();

                matchString += generateTeamName(team, false, teams.get(t).getType());

                if (!teams.get(t).getEntourage().isEmpty()) {
                    matchString += " w/ " + slashShortNames(teams.get(t).getEntourage());
                }

                if (t == 0 && !matchString.isEmpty()) {
                    matchString += " vs ";

                } else if (t < teamsSize - 1 && !matchString.isEmpty()) {
                    matchString += ", ";
                }

            }
        } else {
            //probable placeholder
            matchString += !teams.isEmpty() ? teams.get(0) : "";
        }

        if (matchString.isEmpty()) {

            matchString += "Empty Match";
        }

        return matchString;

    }

    public String getMatchString(SegmentView segmentView, boolean verbose) {
        List<SegmentTeam> teams = segmentView.getTeams();
        MatchFinish finish = ((Match) segmentView.getSegment()).getSegmentParams().getMatchFinish();
        int teamsSize = segmentView.getMatchParticipantTeams().size();
        String matchString = "";

        if (teamsSize > 1) {

            for (int t = 0; t < teamsSize; t++) {
                List<WorkerView> team = teams.get(t).getWorkers();

                matchString += generateTeamName(team, verbose, teams.get(t).getType());

                if (!teams.get(t).getEntourage().isEmpty()) {
                    matchString += " w/ " + slashShortNames(teams.get(t).getEntourage());
                }

                if (t == 0 && !matchString.isEmpty()) {
                    matchString += " def. ";

                } else if (t < teamsSize - 1 && !matchString.isEmpty()) {
                    matchString += ", ";
                }

            }

            switch (finish) {
                case COUNTOUT:
                    matchString += " by Countout";
                    break;
                case DQINTERFERENCE:
                case DQ:
                    matchString += " by DQ";
                    break;
                default:
                    break;

            }

        } else {
            //probable placeholder
            matchString += !teams.isEmpty() ? teams.get(0) : "";
        }

        if (finish != null && finish.equals(MatchFinish.DRAW)) {
            matchString = matchString.replace("def.", "drew");
        }

        if (matchString.isEmpty()) {

            matchString += "Empty Match";
        }

        return matchString;

    }
}
