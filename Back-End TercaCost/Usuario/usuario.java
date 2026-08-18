package Usuario;

public class usuario {
    String nome;
    String email;
    String senha;
    String projeto;

    public usuario(String nome, String email, String senha, String projeto) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.projeto = projeto;
    }


    //Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getProjeto() {
        return projeto;
    }

    public void setProjeto(String projeto) {
        this.projeto = projeto;
    }

    
}
