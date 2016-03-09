package jwwu.com.dotabuddy.dota_logic;


import java.util.ArrayList;

public class HeroStats {
    //TODO its better to use a List.. delete old variables and edit DBUpdaterActivity so it adds Stat Objects to list
    public ArrayList<Stat> statArrayList;
    protected String primaryAttribute;      //1
    protected String strength;              //2
    protected String strengthGrow;          //3
    protected String agility;               //4
    protected String agilityGrow;           //5
    protected String intelligence;          //6
    protected String intelligenceGrow;      //7
    protected String damageMin;             //8
    protected String damageMax;             //9
    protected String armor;                 //10
    protected String moveSpeed;             //11
    protected String attackRange;           //12
    protected String attackPoint;           //13
    protected String attackBackswing;       //14
    protected String bat;                   //15
    protected String missileSpeed;          //16
    protected String sightRangeDay;         //17
    protected String sightRangeNight;       //18
    protected String turnRate;              //19
    protected String collisionSize;         //20
    private static String strSeparator = "__,__";

    public HeroStats(String primaryAttribute, String strength, String strengthGrow, String agility,
                     String agilityGrow, String intelligence, String intelligenceGrow,
                     String damageMin, String damageMax, String armor, String moveSpeed,
                     String attackRange, String attackPoint, String attackBackswing, String bat,
                     String missileSpeed, String sightRangeDay, String sightRangeNight,
                     String turnRate, String collisionSize) {
        this.primaryAttribute = primaryAttribute;
        this.strength = strength;
        this.strengthGrow = strengthGrow;
        this.agility = agility;
        this.agilityGrow = agilityGrow;
        this.intelligence = intelligence;
        this.intelligenceGrow = intelligenceGrow;
        this.damageMin = damageMin;
        this.damageMax = damageMax;
        this.armor = armor;
        this.moveSpeed = moveSpeed;
        this.attackRange = attackRange;
        this.attackPoint = attackPoint;
        this.attackBackswing = attackBackswing;
        this.bat = bat;
        this.missileSpeed = missileSpeed;
        this.sightRangeDay = sightRangeDay;
        this.sightRangeNight = sightRangeNight;
        this.turnRate = turnRate;
        this.collisionSize = collisionSize;
        this.statArrayList = new ArrayList<>();
        this.statArrayList.add(new Stat("Primary Attribute",primaryAttribute));
        this.statArrayList.add(new Stat("Strength",strength));
        this.statArrayList.add(new Stat("StrengthGrow",strengthGrow));
        this.statArrayList.add(new Stat("Agility",agility));
        this.statArrayList.add(new Stat("AgilityGrow",agilityGrow));
        this.statArrayList.add(new Stat("Intelligence",intelligence));
        this.statArrayList.add(new Stat("IntelligenceGrow",intelligenceGrow));
        this.statArrayList.add(new Stat("DamageMin",damageMin));
        this.statArrayList.add(new Stat("DamageMax",damageMax));
        this.statArrayList.add(new Stat("Armor",armor));
        this.statArrayList.add(new Stat("Movespeed",moveSpeed));
        this.statArrayList.add(new Stat("Attackrange",attackRange));
        this.statArrayList.add(new Stat("ArrackPoint",attackPoint));
        this.statArrayList.add(new Stat("AttackBackSwing",attackBackswing));
        this.statArrayList.add(new Stat("BaseAttackTime",bat));
        this.statArrayList.add(new Stat("Missile Speed",missileSpeed));
        this.statArrayList.add(new Stat("SightRangeDay",sightRangeDay));
        this.statArrayList.add(new Stat("SightRangeNight",sightRangeNight));
        this.statArrayList.add(new Stat("Turnrate",turnRate));
        this.statArrayList.add(new Stat("Collisionsize",collisionSize));
    }

    public HeroStats() {
        this.statArrayList = new ArrayList<>();
    }

    public static final String getStringRepresentation(HeroStats stats){
        String[] array = {stats.primaryAttribute, stats.strength, stats.strengthGrow, stats.agility,
                stats.agilityGrow, stats.intelligence, stats.intelligenceGrow, stats.damageMin,
                stats.damageMax, stats.armor, stats.moveSpeed, stats.attackRange, stats.attackPoint,
                stats.attackBackswing, stats.bat, stats.missileSpeed, stats.sightRangeDay,
                stats.sightRangeNight, stats.turnRate, stats.collisionSize};

        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }



    public static HeroStats convertStringToHeroStats(String str){
        String[] arr = str.split(strSeparator);
        return new HeroStats(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7],
                arr[8], arr[9], arr[10], arr[11], arr[12], arr[13], arr[14], arr[15], arr[16],
                arr[17], arr[18], arr[19]);
    }

    public String[] getAllStatsWithText() {
        String[] arr = {"PrimaryAttribute: "+getPrimaryAttribute(),"Strength: "+getStrength(), "StrengthGrow: "+getStrengthGrow(),"Agility: "+getAgility(),"AgilityGrow: "+getAgilityGrow(),"Intelligence: "+getIntelligence(),
        "IntelligenceGrow: "+getIntelligenceGrow(),"DamageMin: "+getDamageMin(),"DamageMax: "+getDamageMax(),"Armor: "+getArmor(),"Movespeed: "+getMoveSpeed(), "Attackrange: "+getAttackRange(),"AttackPoint: "+getAttackPoint(),
        "AttackBackswing: "+getAttackBackswing(),"BaseAttackTime: "+getBat(),"Missile Speed: "+getMissileSpeed(),"SightRangeDay: "+getSightRangeDay(),"SightRangeNight: "+getSightRangeNight(),"Turnrate: "+getTurnRate(),"Collisionsize: "+getCollisionSize()};
        return arr;
    }

    public String getPrimaryAttribute() {
        return primaryAttribute;
    }

    public void setPrimaryAttribute(String primaryAttribute) {
        this.primaryAttribute = primaryAttribute;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getStrengthGrow() {
        return strengthGrow;
    }

    public void setStrengthGrow(String strengthGrow) {
        this.strengthGrow = strengthGrow;
    }

    public String getAgility() {
        return agility;
    }

    public void setAgility(String agility) {
        this.agility = agility;
    }

    public String getAgilityGrow() {
        return agilityGrow;
    }

    public void setAgilityGrow(String agilityGrow) {
        this.agilityGrow = agilityGrow;
    }

    public String getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(String intelligence) {
        this.intelligence = intelligence;
    }

    public String getIntelligenceGrow() {
        return intelligenceGrow;
    }

    public void setIntelligenceGrow(String intelligenceGrow) {
        this.intelligenceGrow = intelligenceGrow;
    }

    public String getDamageMin() {
        return damageMin;
    }

    public void setDamageMin(String damageMin) {
        this.damageMin = damageMin;
    }

    public String getDamageMax() {
        return damageMax;
    }

    public void setDamageMax(String damageMax) {
        this.damageMax = damageMax;
    }

    public String getArmor() {
        return armor;
    }

    public void setArmor(String armor) {
        this.armor = armor;
    }

    public String getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(String moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public String getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(String attackRange) {
        this.attackRange = attackRange;
    }

    public String getAttackPoint() {
        return attackPoint;
    }

    public void setAttackPoint(String attackPoint) {
        this.attackPoint = attackPoint;
    }

    public String getAttackBackswing() {
        return attackBackswing;
    }

    public void setAttackBackswing(String attackBackswing) {
        this.attackBackswing = attackBackswing;
    }

    public String getBat() {
        return bat;
    }

    public void setBat(String bat) {
        this.bat = bat;
    }

    public String getMissileSpeed() {
        return missileSpeed;
    }

    public void setMissileSpeed(String missileSpeed) {
        this.missileSpeed = missileSpeed;
    }

    public String getSightRangeDay() {
        return sightRangeDay;
    }

    public void setSightRangeDay(String sightRangeDay) {
        this.sightRangeDay = sightRangeDay;
    }

    public String getSightRangeNight() {
        return sightRangeNight;
    }

    public void setSightRangeNight(String sightRangeNight) {
        this.sightRangeNight = sightRangeNight;
    }

    public String getTurnRate() {
        return turnRate;
    }

    public void setTurnRate(String turnRate) {
        this.turnRate = turnRate;
    }

    public String getCollisionSize() {
        return collisionSize;
    }

    public void setCollisionSize(String collisionSize) {
        this.collisionSize = collisionSize;
    }
}
