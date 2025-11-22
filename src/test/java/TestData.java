import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TestData {
    private String project;
    private String tester;
    private List<String> skills;
    private int experience;
    private boolean isCertified;

    public TestData() {}

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }

    public String getTester() { return tester; }
    public void setTester(String tester) { this.tester = tester; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    @JsonProperty("isCertified")
    public boolean isCertified() { return isCertified; }
    public void setCertified(boolean certified) { isCertified = certified; }
}