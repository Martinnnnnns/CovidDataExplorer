import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
public class DataSorterTest {

    private DataSorter dataSorter;
    private LocalDate fromDate = LocalDate.of(2021, 3, 4);
    private LocalDate toDate = LocalDate.of(2022, 3, 16);
    @BeforeEach
    public void setUp() {
        dataSorter = new DataSorter();
    }

    @AfterEach
    public void tearDown() {
        dataSorter = null;
    }

    // Test case for filtering data by date range
    @Test
    public void testFilterDataByDateRange() {
        
        ArrayList<CovidData> filteredData = dataSorter.filterDataByDateRange(fromDate, toDate);
        assertNotNull(filteredData);
        assertFalse(filteredData.isEmpty());
        assertEquals(-32,filteredData.get(8).getTransitGMR());
    }
    
    // Test case for getting data by borough
    @Test
    public void testGetDataByBorough() {
        
        String boroughName = "Croydon";
        ArrayList<CovidData> boroughData = dataSorter.getDataByBorough(fromDate, toDate, boroughName);
        assertNotNull(boroughData);
        assertFalse(boroughData.isEmpty());
        assertEquals(-34,boroughData.get(9).getTransitGMR());
    }
    
    // Test case for getting total deaths by date range
    @Test
    public void testGetTotalDeathsByDateRange() {
        
        int totalDeaths = dataSorter.getTotalDeathsByDateRange(fromDate, toDate);
        assertEquals(3997, totalDeaths);
    }
    
    // Test case for getting average cases
    @Test
    public void testGetAverageCases() {
        
        double averageCases = dataSorter.getAverageCases(fromDate, toDate);
        assertEquals(162.0, averageCases); 
    }
    
    // Test case for getting average retail GMR
    @Test
    public void testGetAverageRetailGMR() {
        
        double averageRetailGMR = dataSorter.getAverageRetailGMR(fromDate, toDate);
        assertEquals(-26.04, averageRetailGMR); 
    }
    
    // Test case for getting average grocery GMR
    @Test
    public void testGetAverageGroceryGMR() {
        
        double averageGroceryGMR = dataSorter.getAverageGroceryGMR(fromDate, toDate);
        assertEquals(-4.89, averageGroceryGMR);
    }
    
    // Test case for getting cumulative death by intervals
    @Test
    public void testGetCumulativeDeathByIntervals() {
        
        String boroughName = "Sutton";
        Map<LocalDate, Integer> cumulativeDeathsMap = dataSorter.getCumulativeDeathByIntervals(fromDate, toDate, boroughName);
        List<Entry<LocalDate, Integer>> entryList = cumulativeDeathsMap.entrySet().stream().collect(Collectors.toList());
        Entry<LocalDate, Integer> sixthEntry = entryList.get(5);
        Integer sixthValue = sixthEntry.getValue();
        assertNotNull(cumulativeDeathsMap);
        assertFalse(cumulativeDeathsMap.isEmpty());
        assertEquals(188,sixthValue);
    }
    //Test case for getting cumulative cases by intervals
    @Test
    public void testGetCumulativeCasesByIntervals() {
        
        String boroughName = "Sutton";
        Map<LocalDate, Integer> cumulativeCasesMap = dataSorter.getCumulativeCasesByIntervals(fromDate, toDate, boroughName);
        List<Entry<LocalDate, Integer>> entryList = cumulativeCasesMap.entrySet().stream().collect(Collectors.toList());
        Entry<LocalDate, Integer> sixthEntry = entryList.get(5);
        Integer sixthValue = sixthEntry.getValue();
        assertNotNull(cumulativeCasesMap);
        assertFalse(cumulativeCasesMap.isEmpty());
        assertEquals(96874,sixthValue);
    }
}
