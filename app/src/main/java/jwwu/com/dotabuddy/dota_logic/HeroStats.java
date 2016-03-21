package jwwu.com.dotabuddy.dota_logic;


import android.util.SparseArray;

import java.util.ArrayList;

public class HeroStats {
    private SparseArray<Stat> statSparseArray;


    private static final String STR_SEPERATOR = "__,__";

    private static final String PRIMARYATTRIBUTENAME = "Primary Attribute";     //1
    private static final int PRIMARYATTRIBUTEINDEX = 0;

    private static final String STRENGTHNAME = "Strength";                      //2
    private static final int STRENGTHINDEX = 1;

    private static final String STRENGTHGROWNAME = "StrengthGrow";              //3
    private static final int STRENGTHGROWINDEX = 2;

    private static final String AGILITYNAME = "Agility";                        //4
    private static final int AGILITYINDEX = 3;

    private static final String AGILITYGROWNAME = "AgilityGrow";                //5
    private static final int AGILITYGROWINDEX = 4;

    private static final String INTELLIGENCENAME = "Intelligence";              //6
    private static final int INTELLIGENCEINDEX = 5;

    private static final String INTELLIGENCEGROWNAME = "IntelligenceGrow";      //7
    private static final int INTELLIGENCEGROWINDEX = 6;

    private static final String DAMAGEMINNAME = "DamageMin";                    //8
    private static final int DAMAGEMININDEX = 7;

    private static final String DAMAGEMAXNAME = "DamageMax";                    //9
    private static final int DAMAGEMAXINDEX = 8;

    private static final String ARMORNAME = "Armor";                            //10
    private static final int ARMORINDEX = 9;

    private static final String MOVESPEEDNAME = "Movespeed";                    //11
    private static final int MOVESPEEDINDEX = 10;

    private static final String ATTACKRANGENAME = "Attackrange";                //12
    private static final int ATTACKRANGEINDEX = 11;

    private static final String ATTACKPOINTNAME = "AttackPoint";                //13
    private static final int ATTACKPOINTINDEX = 12;

    private static final String ATTACKBACKSWINGNAME = "AttackBackSwing";        //14
    private static final int ATTACKBACKSWINGINDEX = 13;

    private static final String BASEATTACKTIMENAME = "BaseAttackTime";          //15
    private static final int BASEATTACKTIMEINDEX = 14;

    private static final String MISSILESPEEDNAME = "Missile Speed";             //16
    private static final int MISSILESPEEDINDEX = 15;

    private static final String SIGHTRANGEDAYNAME = "SightRangeDay";            //17
    private static final int SIGHTRANGEDAYINDEX = 16;

    private static final String SIGHTRANGENIGHTNAME = "SightRangeNight";        //18
    private static final int SIGHTRANGENIGHTINDEX = 17;

    private static final String TURNRATENAME = "Turnrate";                      //19
    private static final int TURNRATEINDEX = 18;

    private static final String COLLISIONSIZENAME = "Collisionsize";            //20
    private static final int COLLISIONSIZEINDEX = 19;

    public HeroStats(String primaryAttribute, String strength, String strengthGrow, String agility,
                     String agilityGrow, String intelligence, String intelligenceGrow,
                     String damageMin, String damageMax, String armor, String moveSpeed,
                     String attackRange, String attackPoint, String attackBackswing, String bat,
                     String missileSpeed, String sightRangeDay, String sightRangeNight,
                     String turnRate, String collisionSize) {
        this.statSparseArray = new SparseArray<>();
        setPrimaryAttribute(primaryAttribute);
        setStrength(strength);
        setStrengthGrow(strengthGrow);
        setAgility(agility);
        setAgilityGrow(agilityGrow);
        setIntelligence(intelligence);
        setIntelligenceGrow(intelligenceGrow);
        setDamageMin(damageMin);
        setDamageMax(damageMax);
        setArmor(armor);
        setMoveSpeed(moveSpeed);
        setAttackRange(attackRange);
        setAttackPoint(attackPoint);
        setAttackBackswing(attackBackswing);
        setBat(bat);
        setMissileSpeed(missileSpeed);
        setSightRangeDay(sightRangeDay);
        setSightRangeNight(sightRangeNight);
        setTurnRate(turnRate);
        setCollisionSize(collisionSize);
    }

    public HeroStats(String dbString) {
        this.statSparseArray = new SparseArray<>();
        String[] arr = dbString.split(STR_SEPERATOR);
        setPrimaryAttribute(arr[PRIMARYATTRIBUTEINDEX]);
        setStrength(arr[STRENGTHINDEX]);
        setStrengthGrow(arr[STRENGTHGROWINDEX]);
        setAgility(arr[AGILITYINDEX]);
        setAgilityGrow(arr[AGILITYGROWINDEX]);
        setIntelligence(arr[INTELLIGENCEINDEX]);
        setIntelligenceGrow(arr[INTELLIGENCEGROWINDEX]);
        setDamageMin(arr[DAMAGEMININDEX]);
        setDamageMax(arr[DAMAGEMAXINDEX]);
        setArmor(arr[ARMORINDEX]);
        setMoveSpeed(arr[MOVESPEEDINDEX]);
        setAttackRange(arr[ATTACKRANGEINDEX]);
        setAttackPoint(arr[ATTACKPOINTINDEX]);
        setAttackBackswing(arr[ATTACKBACKSWINGINDEX]);
        setBat(arr[BASEATTACKTIMEINDEX]);
        setMissileSpeed(arr[MISSILESPEEDINDEX]);
        setSightRangeDay(arr[SIGHTRANGEDAYINDEX]);
        setSightRangeNight(arr[SIGHTRANGENIGHTINDEX]);
        setTurnRate(arr[TURNRATEINDEX]);
        setCollisionSize(arr[COLLISIONSIZEINDEX]);
    }

    public HeroStats() {
        this.statSparseArray = new SparseArray<>();
    }

    public static final String getStringRepresentation(HeroStats stats){
        String[] array = {stats.getPrimaryAttribute(), stats.getStrength(), stats.getStrengthGrow(), stats.getAgility(),
                stats.getAgilityGrow(), stats.getIntelligence(), stats.getIntelligenceGrow(), stats.getDamageMin(),
                stats.getDamageMax(), stats.getArmor(), stats.getMoveSpeed(), stats.getAttackRange(), stats.getAttackPoint(),
                stats.getAttackBackswing(), stats.getBat(), stats.getMissileSpeed(), stats.getSightRangeDay(),
                stats.getSightRangeNight(), stats.getTurnRate(), stats.getCollisionSize()};

        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+ STR_SEPERATOR;
            }
        }
        return str;
    }

    public ArrayList<Stat> getStatList() {
        ArrayList<Stat> statlist = new ArrayList<>();
        for(int i=0; i<statSparseArray.size(); i++) {
            statlist.add(statSparseArray.valueAt(i));
        }
        return statlist;
    }

    public static HeroStats convertStringToHeroStats(String str){
        String[] arr = str.split(STR_SEPERATOR);
        return new HeroStats(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7],
                arr[8], arr[9], arr[10], arr[11], arr[12], arr[13], arr[14], arr[15], arr[16],
                arr[17], arr[18], arr[19]);
    }

    public String getPrimaryAttribute() {
        return statSparseArray.get(PRIMARYATTRIBUTEINDEX).getValue();
    }

    public void setPrimaryAttribute(String primaryAttribute) {
        this.statSparseArray.put(PRIMARYATTRIBUTEINDEX, new Stat(PRIMARYATTRIBUTENAME,primaryAttribute));
    }

    public String getStrength() {
        return statSparseArray.get(STRENGTHINDEX).getValue();
    }

    public void setStrength(String strength) {
        statSparseArray.put(STRENGTHINDEX,new Stat(STRENGTHNAME, strength));
    }

    public String getStrengthGrow() {
        return statSparseArray.get(STRENGTHGROWINDEX).getValue();
    }

    public void setStrengthGrow(String strengthGrow) {
        statSparseArray.put(STRENGTHGROWINDEX,new Stat(STRENGTHGROWNAME, strengthGrow));
    }

    public String getAgility() {
        return statSparseArray.get(AGILITYINDEX).getValue();
    }

    public void setAgility(String agility) {
        statSparseArray.put(AGILITYINDEX,new Stat(AGILITYNAME, agility));
    }

    public String getAgilityGrow() {
        return statSparseArray.get(AGILITYGROWINDEX).getValue();
    }

    public void setAgilityGrow(String agilityGrow) {
        statSparseArray.put(AGILITYGROWINDEX,new Stat(AGILITYGROWNAME, agilityGrow));
    }

    public String getIntelligence() {
        return statSparseArray.get(INTELLIGENCEINDEX).getValue();
    }

    public void setIntelligence(String intelligence) {
        statSparseArray.put(INTELLIGENCEINDEX,new Stat(INTELLIGENCENAME, intelligence));
    }

    public String getIntelligenceGrow() {
        return statSparseArray.get(INTELLIGENCEGROWINDEX).getValue();
    }

    public void setIntelligenceGrow(String intelligenceGrow) {
        statSparseArray.put(INTELLIGENCEGROWINDEX,new Stat(INTELLIGENCEGROWNAME, intelligenceGrow));
    }

    public String getDamageMin() {
        return statSparseArray.get(DAMAGEMAXINDEX).getValue();
    }

    public void setDamageMin(String damageMin) {
        statSparseArray.put(DAMAGEMININDEX,new Stat(DAMAGEMINNAME, damageMin));
    }

    public String getDamageMax() {
        return statSparseArray.get(DAMAGEMAXINDEX).getValue();
    }

    public void setDamageMax(String damageMax) {
        statSparseArray.put(DAMAGEMAXINDEX,new Stat(DAMAGEMAXNAME, damageMax));
    }

    public String getArmor() {
        return statSparseArray.get(ARMORINDEX).getValue();
    }

    public void setArmor(String armor) {
        statSparseArray.put(ARMORINDEX,new Stat(ARMORNAME, armor));
    }

    public String getMoveSpeed() {
        return statSparseArray.get(MOVESPEEDINDEX).getValue();
    }

    public void setMoveSpeed(String moveSpeed) {
        statSparseArray.put(MOVESPEEDINDEX,new Stat(MOVESPEEDNAME, moveSpeed));
    }

    public String getAttackRange() {
        return statSparseArray.get(ATTACKRANGEINDEX).getValue();
    }

    public void setAttackRange(String attackRange) {
        statSparseArray.put(ATTACKRANGEINDEX,new Stat(ATTACKRANGENAME, attackRange));
    }

    public String getAttackPoint() {
        return statSparseArray.get(ATTACKPOINTINDEX).getValue();
    }

    public void setAttackPoint(String attackPoint) {
        statSparseArray.put(ATTACKPOINTINDEX,new Stat(ATTACKPOINTNAME, attackPoint));
    }

    public String getAttackBackswing() {
        return statSparseArray.get(ATTACKBACKSWINGINDEX).getValue();
    }

    public void setAttackBackswing(String attackBackswing) {
        statSparseArray.put(ATTACKBACKSWINGINDEX,new Stat(ATTACKBACKSWINGNAME, attackBackswing));
    }

    public String getBat() {
        return statSparseArray.get(BASEATTACKTIMEINDEX).getValue();
    }

    public void setBat(String bat) {
        statSparseArray.put(BASEATTACKTIMEINDEX,new Stat(BASEATTACKTIMENAME, bat));
    }

    public String getMissileSpeed() {
        return statSparseArray.get(MISSILESPEEDINDEX).getValue();
    }

    public void setMissileSpeed(String missileSpeed) {
        statSparseArray.put(MISSILESPEEDINDEX,new Stat(MISSILESPEEDNAME, missileSpeed));
    }

    public String getSightRangeDay() {
        return statSparseArray.get(SIGHTRANGEDAYINDEX).getValue();
    }

    public void setSightRangeDay(String sightRangeDay) {
        statSparseArray.put(SIGHTRANGEDAYINDEX,new Stat(SIGHTRANGEDAYNAME, sightRangeDay));
    }

    public String getSightRangeNight() {
        return statSparseArray.get(SIGHTRANGENIGHTINDEX).getValue();
    }

    public void setSightRangeNight(String sightRangeNight) {
        statSparseArray.put(SIGHTRANGENIGHTINDEX,new Stat(SIGHTRANGENIGHTNAME, sightRangeNight));
    }

    public String getTurnRate() {
        return statSparseArray.get(TURNRATEINDEX).getValue();
    }

    public void setTurnRate(String turnRate) {
        statSparseArray.put(TURNRATEINDEX,new Stat(TURNRATENAME, turnRate));
    }

    public String getCollisionSize() {
        return statSparseArray.get(COLLISIONSIZEINDEX).getValue();
    }

    public void setCollisionSize(String collisionSize) {
        statSparseArray.put(COLLISIONSIZEINDEX,new Stat(COLLISIONSIZENAME, collisionSize));
    }
}
