package aluno.ifsc.app.focos.dengue.model.state;

import com.google.gson.annotations.SerializedName;

public class State {

    @SerializedName("codigo_uf")
    private Integer codeState;

    @SerializedName("uf")
    private String initials;

    @SerializedName("nome")
    private String name;

    public Integer getCodeState() {
        return codeState;
    }

    public void setCodeState(Integer codeState) {
        this.codeState = codeState;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}