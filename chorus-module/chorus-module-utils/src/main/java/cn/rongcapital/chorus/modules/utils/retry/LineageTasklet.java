package cn.rongcapital.chorus.modules.utils.retry;

import cn.rongcapital.chorus.modules.utils.retry.constants.LineageVerTexType;
import com.google.common.base.Joiner;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by abiton on 14/08/2017.
 */
public abstract class LineageTasklet extends SimpleTasklet {

    @Override
    public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception {
        RepeatStatus repeatStatus = executeWithLineage(chunkContext, contribution);
        after(chunkContext);
        return repeatStatus;
    }

    private void after(ChunkContext chunkContext) throws Exception {
        ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
        executionContext.put("inputs", inputs(chunkContext).getVertexes());
        executionContext.put("outputs", outputs(chunkContext).getVertexes());
    }

    protected abstract Vertex inputs(ChunkContext chunkContext);

    protected abstract Vertex outputs(ChunkContext chunkContext);

    public abstract RepeatStatus executeWithLineage(ChunkContext chunkContext, StepContribution contribution) throws Exception;

    protected class Vertex {
        private Map<String, Object> vertexes = new LinkedHashMap<>();

        protected void set(String key, Object value) {
            this.vertexes.put(key, value);
        }

        private Map<String, Object> getVertexes() {
            return vertexes;
        }
    }


    private class TableVertex extends Vertex {
        private Set<String> tables = new HashSet<>();

        public TableVertex() {
            this.set("tables", tables);
        }

        public void add(String database, String table) {
            tables.add(combine(database, table));
        }

        //<projectId>:<connect_user>:<url>:<tableName>
        public void add(String projectId, String rdbName, String connectUser, String url, String tableName) {
            tables.add(Joiner.on(",").join(new String[]{projectId, rdbName, connectUser, url, tableName}));
        }

        private String combine(String database, String table) {
            return database + "." + table;
        }
    }

    protected class InternalTableVertex extends TableVertex {
        private final String TYPE = LineageVerTexType.INTERNAL_TABLE.name();

        public InternalTableVertex() {
            this.set("type", TYPE);
        }
    }

    protected class ExternalTableVertex extends TableVertex {
        private final String TYPE = LineageVerTexType.EXTERNAL_TABLE.name();

        public ExternalTableVertex() {
            this.set("type", TYPE);
        }
    }
}

