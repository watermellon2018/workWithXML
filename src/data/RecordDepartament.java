package data;

public class RecordDepartament implements Comparable<RecordDepartament> {
    private String code, job, comment;

    public RecordDepartament(String code, String job, String comment){
        this.code = code;
        this.job = job;
        this.comment = comment;
    }

    public String getCode(){
        return code;
    }

    public String getJob(){
        return job;
    }

    public String getComment(){
        return comment;
    }

    @Override
    public String toString(){
        return code+" "+job+" "+comment;
    }

    @Override
    public int compareTo(RecordDepartament recordDepartament) {

        int eq = code.compareTo(recordDepartament.code);

        if(eq == 0){
            eq = job.compareTo(recordDepartament.job);

            if(eq == 0){
                eq = comment.compareTo(recordDepartament.comment);
                return eq;

            }else
                return eq;
        }else{
            return eq;
        }

    }

    /** смотрит та же это запись, только с обновленными данными или нет*/
    public boolean isUpdating(RecordDepartament x){

        if(code.equals(x.code) && job.equals(x.job) && !comment.equals(x.comment))
            return true;

        return false;
    }

    @Override
    public boolean equals(Object y) {
        if(this == y)
            return true;

        if(getClass() != y.getClass() || y == null)
            return false;

        RecordDepartament x = (RecordDepartament) y;

        return code.equals(x.code) && job.equals(x.job) && comment.equals(x.comment);

    }

    @Override
    public int hashCode(){
        return 31 * (code.hashCode() + job.hashCode() + comment.hashCode());
    }
}
