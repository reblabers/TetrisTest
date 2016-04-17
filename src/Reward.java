import pentmino.enums.PentminoType;

public class Reward {
    private final PentminoType pentminoType;
    private final RewardType rewardType;

    public Reward(PentminoType pentminoType, RewardType rewardType) {
        this.pentminoType = pentminoType;
        this.rewardType = rewardType;
    }
}