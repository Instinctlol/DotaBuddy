package jwwu.com.dotabuddy.dota_logic;


import android.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Instinctlol on 02.01.2016.
 */
public class Balancechangelog{
    private ArrayList<Line> balancechangelog;
    private static String strSeparator = "__,__";
    private static String lineSeperator = "__LINE__";

    public Balancechangelog() {
        this.balancechangelog = new ArrayList<>();
    }

    public void feedNonParsedLine(String line) {
        if(!line.isEmpty()) {
            if(line.startsWith("'''")) {
                line=line.replace("'","");
                line=line.replace("[","");
                line=line.replace("]","");
                Line l = new Line();
                l.setText(line);
                l.setHidden(true);
                l.setIndentlevel(0);
                balancechangelog.add(l);
            }
            else {
                Pair<Integer,Boolean> p=findIndentLevelAndHiddenStatus(line);
                String text=line.substring(p.first+1,line.length());
                Line l = new Line();
                l.setText(text);
                l.setHidden(p.second);
                l.setIndentlevel(p.first);
                balancechangelog.add(l);
            }
        }
        else
        {
            Line l = new Line();
            l.setText("");
            l.setIndentlevel(0);
            l.setHidden(true);
            balancechangelog.add(l);
        }
    }



    public boolean removeLine(int index) {
        Line l = balancechangelog.get(index);
        return balancechangelog.remove(l);
    }

    public int getIndentlevel(int index) {
        return balancechangelog.get(index).getIndentlevel();
    }

    public boolean getHiddenstatus(int index) {
        return balancechangelog.get(index).isHidden();
    }

    public String getText(int index) {
        return balancechangelog.get(index).getText();
    }

    public int size() {
        return balancechangelog.size();
    }

    public boolean isEmpty() {
        return balancechangelog.isEmpty();
    }

    public boolean isEmptyLine(int index) {
        return balancechangelog.get(index).isEmpty();
    }

    //e.g. TEXT__,__9999__,__true__LINE__TEXT2__,__4200__,__false__LINE__TEXT3__,__1234__,__true
    public String getStringRepresentation() {
        String str="";

        for(int i = 0; i<balancechangelog.size(); i++) {
            Line l = balancechangelog.get(i);

            if(i<balancechangelog.size()-1)
                str+=l.getText()+strSeparator+l.indentlevel+strSeparator+l.isHidden()+lineSeperator;
            else
                str+=l.getText()+strSeparator+l.indentlevel+strSeparator+l.isHidden();
        }
        return str;
    }

    public void buildFromStringRepresentation(String stringRepresentation) {
        String[] lines = stringRepresentation.split(lineSeperator);


        for(String line : lines) {
            String[] lineComponents = line.split(strSeparator);

            Line l = new Line();
            l.setText(lineComponents[0]);
            l.setIndentlevel(Integer.parseInt(lineComponents[1]));
            l.setHidden(Boolean.parseBoolean(lineComponents[2]));

            balancechangelog.add(l);
        }
    }



    private Pair<Integer,Boolean> findIndentLevelAndHiddenStatus(String line) {
        int indentlevel=0;
        boolean hidden=false;

        for(char c : line.toCharArray()) {
            switch(c) {
                case '*':
                    indentlevel++;
                    break;
                case ':':
                    indentlevel++;
                    hidden=true;
                    break;
                default:
                    return new Pair<Integer,Boolean>(indentlevel,hidden);
            }
        }

        return null;
    }

    public Iterator<String> textIterator() {
        return new Iterator<String>() {
            int current=0;

            @Override
            public boolean hasNext() {
                return balancechangelog.size() > 0 && current<balancechangelog.size() && balancechangelog.get(current) != null;
            }

            @Override
            public String next() {
                return balancechangelog.get(current++).getText();
            }

            @Override
            public void remove() {
                balancechangelog.remove(current);
            }
        };
    }

    public Iterator<Integer> indentlevelIterator() {
        return new Iterator<Integer>() {

            int current=0;

            @Override
            public boolean hasNext() {
                return balancechangelog.size() > 0 && current<balancechangelog.size() && balancechangelog.get(current) != null;
            }

            @Override
            public Integer next() {
                return balancechangelog.get(current++).getIndentlevel();
            }

            @Override
            public void remove() {
                balancechangelog.remove(current);
            }
        };
    }

    public Iterator<Boolean> hiddenStatusIterator() {
        return new Iterator<Boolean>() {
            int current=0;

            @Override
            public boolean hasNext() {
                return balancechangelog.size() > 0 && current<balancechangelog.size() && balancechangelog.get(current) != null;
            }

            @Override
            public Boolean next() {
                return balancechangelog.get(current++).isHidden();
            }

            @Override
            public void remove() {
                balancechangelog.remove(current);
            }
        };
    }

    private class Line  {
        private String text;
        private Integer indentlevel;
        private boolean hidden;         //some lines have no dot

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Integer getIndentlevel() {
            return indentlevel;
        }

        public void setIndentlevel(Integer indentlevel) {
            this.indentlevel = indentlevel;
        }

        public boolean isHidden() {
            return hidden;
        }

        public void setHidden(boolean hidden) {
            this.hidden = hidden;
        }

        public boolean isEmpty() {
            return text.isEmpty();
        }
    }

}
