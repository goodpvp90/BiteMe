package containers;

import java.util.List;

public class ListContainer<T> {
    private List<T> list;

    public ListContainer(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
