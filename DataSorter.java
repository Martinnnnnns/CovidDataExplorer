import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.text.DecimalFormat;

public class DataSorter {

    private CovidDataLoader loader;
    private int intervals = 5;
    private DecimalFormat df = new DecimalFormat("#.##");

    public DataSorter() {
        this.loader = new CovidDataLoader();
    }

    // get data by date range
    public ArrayList<CovidData> filterDataByDateRange(LocalDate fromDate, LocalDate toDate) {
        ArrayList<CovidData> allData = loader.load(); // Load all data
        ArrayList<CovidData> filteredData = new ArrayList<>();

        for (CovidData data : allData) {
            LocalDate date = LocalDate.parse(data.getDate());
            if (!date.isBefore(fromDate) && !date.isAfter(toDate)) {
                filteredData.add(data);
            }
        }
        return filteredData;
    }
    
    //get data by borough
    public ArrayList<CovidData> getDataByBorough(LocalDate fromDate, LocalDate toDate, String boroughName){
        ArrayList<CovidData> filteredData = filterDataByDateRange(fromDate, toDate);
        ArrayList<CovidData> boroughData = new ArrayList<>();
        for (CovidData data : filteredData) {
            if (data.getBorough().equalsIgnoreCase(boroughName)) {
                boroughData.add(data);
            }
        }
        return boroughData;
    }
    //get total deaths by date range
    public int getTotalDeathsByDateRange(LocalDate fromDate, LocalDate toDate) {
        ArrayList<CovidData> filteredData = filterDataByDateRange(fromDate, toDate);
        int totalDeaths = 0;

        for (CovidData data : filteredData) {
            totalDeaths += data.getNewDeaths();
        }

        return totalDeaths;
    }

    //get average cases by date range
    public double getAverageCases(LocalDate fromDate, LocalDate toDate) {
        ArrayList<CovidData> filteredData = filterDataByDateRange(fromDate, toDate);
        int totalCases = 0;
        int divider = 0;
        for (CovidData data : filteredData) {
            totalCases += data.getNewCases();
            divider++;
        }
        double averageCases = totalCases / divider;
        return Double.parseDouble(df.format(averageCases));
    }

    //following methods are all to get specific GMR date for a specific borough
    public double getAverageRetailGMR(LocalDate fromDate, LocalDate toDate) {
        ArrayList<CovidData> filteredData = filterDataByDateRange(fromDate, toDate);
        double totalGMR = 0;
        int divider = 0;
        for (CovidData data : filteredData) {
            totalGMR += data.getRetailRecreationGMR();
            divider++;
        }
        double averageGMR = totalGMR / divider;
        return Double.parseDouble(df.format(averageGMR));
    }

    public double getAverageGroceryGMR(LocalDate fromDate, LocalDate toDate) {
        ArrayList<CovidData> filteredData = filterDataByDateRange(fromDate, toDate);
        double totalGMR = 0;
        int divider = 0;
        for (CovidData data : filteredData) {
            totalGMR += data.getGroceryPharmacyGMR();
            divider++;
        }
        double averageGMR = totalGMR / divider;
        return Double.parseDouble(df.format(averageGMR));
    }

    public double getBoroughGroceryGMR(LocalDate fromDate, LocalDate toDate, String boroughName) {
        ArrayList<CovidData> filteredData = getDataByBorough(fromDate, toDate, boroughName);
        double totalGMR = 0;
        int divider = 0;
        for (CovidData data : filteredData) {
            totalGMR += data.getGroceryPharmacyGMR();
            divider++;
        }

        return (totalGMR / divider);
    }

    public double getBoroughRetailGMR(LocalDate fromDate, LocalDate toDate, String boroughName) {
        ArrayList<CovidData> filteredData = getDataByBorough(fromDate, toDate, boroughName);
        double totalGMR = 0;
        int divider = 0;
        for (CovidData data : filteredData) {
            totalGMR += data.getRetailRecreationGMR();
            divider++;
        }

        return (totalGMR / divider);
    }

    public double getBoroughWorkplaceGMR(LocalDate fromDate, LocalDate toDate, String boroughName) {
        ArrayList<CovidData> filteredData = getDataByBorough(fromDate, toDate, boroughName);
        double totalGMR = 0;
        int divider = 0;
        for (CovidData data : filteredData) {
            totalGMR += data.getWorkplacesGMR();
            divider++;
        }

        return (totalGMR / divider);
    }

    public double getBoroughParksGMR(LocalDate fromDate, LocalDate toDate, String boroughName) {
        ArrayList<CovidData> filteredData = getDataByBorough(fromDate, toDate, boroughName);
        double totalGMR = 0;
        int divider = 0;
        for (CovidData data : filteredData) {
            totalGMR += data.getParksGMR();
            divider++;
        }

        return (totalGMR / divider);
    }

    public double getBoroughTransitGMR(LocalDate fromDate, LocalDate toDate, String boroughName) {
        ArrayList<CovidData> filteredData = getDataByBorough(fromDate, toDate, boroughName);
        double totalGMR = 0;
        int divider = 0;
        for (CovidData data : filteredData) {
            totalGMR += data.getTransitGMR();
            divider++;
        }

        return (totalGMR / divider);
    }

    public double getBoroughResidentialGMR(LocalDate fromDate, LocalDate toDate, String boroughName) {
        ArrayList<CovidData> filteredData = getDataByBorough(fromDate, toDate, boroughName);
        double totalGMR = 0;
        int divider = 0;
        for (CovidData data : filteredData) {
            totalGMR += data.getResidentialGMR();
            divider++;
        }

        return (totalGMR / divider);
    }
    // method to get a map of dates and cumulative deaths by date range

    public Map<LocalDate, Integer> getCumulativeDeathByIntervals(LocalDate fromDate, LocalDate toDate, String boroughName) 
    {
        Map<LocalDate, Integer> cumulativeDeathsMap = new LinkedHashMap<>();

        long daysBetween = ChronoUnit.DAYS.between(fromDate, toDate);
        int cumulativeDeaths = 0;
        
        if (daysBetween / intervals ==1 || daysBetween / intervals == 0) {
            // Case when intervals are larger than daysBetween, so we iterate over each day
            for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
                List<CovidData> dailyData = getDataByBorough(date, date, boroughName);
                for (CovidData data : dailyData) {
                    cumulativeDeaths += data.getNewDeaths();
                }
                cumulativeDeathsMap.put(date, cumulativeDeaths);
            }
        } else {
            long daysPerInterval = daysBetween / intervals;
            for (int i = 0; i <= intervals; i++) {
                LocalDate intervalDate = fromDate.plusDays(daysPerInterval * i);
                if (intervalDate.isAfter(toDate)) {
                    intervalDate = toDate;
                }
                List<CovidData> intervalData = getDataByBorough(fromDate, intervalDate, boroughName);
                for (CovidData data : intervalData) {
                    cumulativeDeaths += data.getNewDeaths();
                }
                cumulativeDeathsMap.put(intervalDate, cumulativeDeaths);
                if (intervalDate.equals(toDate)) {
                    break; // Exit the loop if the intervalDate reaches or surpasses toDate
                }
            }
        }
        return cumulativeDeathsMap;
    }
    
    //method to get a map of dates and cumulative new cases by date range
    public Map<LocalDate, Integer> getCumulativeCasesByIntervals(LocalDate fromDate, LocalDate toDate, String boroughName) {
        Map<LocalDate, Integer> cumulativeCasesMap = new LinkedHashMap<>();

        long daysBetween = ChronoUnit.DAYS.between(fromDate, toDate);
        int cumulativeCases = 0;
        
        if (daysBetween / intervals ==1 || daysBetween / intervals == 0) {
            // Case when intervals are larger than daysBetween, so we iterate over each day
            for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
                List<CovidData> dailyData = getDataByBorough(date, date, boroughName);
                for (CovidData data : dailyData) {
                    cumulativeCases += data.getNewCases();
                }
                cumulativeCasesMap.put(date, cumulativeCases);
            }
        } else {
            long daysPerInterval = daysBetween / intervals;
            for (int i = 0; i <= intervals; i++) {
                LocalDate intervalDate = fromDate.plusDays(daysPerInterval * i);
                if (intervalDate.isAfter(toDate)) {
                    intervalDate = toDate;
                }
                List<CovidData> intervalData = getDataByBorough(fromDate, intervalDate, boroughName);
                for (CovidData data : intervalData) {
                    cumulativeCases += data.getNewCases();
                }
                cumulativeCasesMap.put(intervalDate, cumulativeCases);
                if (intervalDate.equals(toDate)) {
                    break; // Exit the loop if the intervalDate reaches or surpasses toDate
                }
            }
        }
        return cumulativeCasesMap;
    }
    
}
