package jwwu.com.dotabuddy.adapters.Lexikon;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

/**
 * Created by Instinctlol on 11.03.2016.
 */
public class LexikonParentListItem implements ParentListItem {

    private String mName;
    private List<LexikonChildWrapper> mLexikonChildWrappers;

    public LexikonParentListItem(String name, List<LexikonChildWrapper> lexikonChildWrappers) {
        mName = name;
        mLexikonChildWrappers = lexikonChildWrappers;
    }

    public String getName() {
        return mName;
    }

    @Override
    public List<?> getChildItemList() {
        return mLexikonChildWrappers;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}