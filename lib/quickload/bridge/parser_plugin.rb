module QuickLoad
  module Bridge
    require 'quickload/java/imports'
    require 'quickload/bridge/data_source'
    require 'quickload/bridge/schema'
    require 'quickload/file_reader'
    require 'quickload/record_writer'

    class ParserPluginBridge < Java::BasicParserPlugin
      def getBasicParserTask(exec, config)
        config = DataSourceBridge.wrap(config)
        task = TaskConfig.new(exec)
        configure(config, task)
        exec.setSchema(Bridge::Schema.new(task.columns).to_java)
        DataSourceBridge.to_java_task_source(task)
      end

      def runBasicParser(exec, taskSource, processorIndex, fileBufferInput, pageOutput)
        task = TaskConfig.new(exec, DataSourceBridge.wrap(taskSource))
        file_reader = FileReader.new(fileBufferInput)
        record_writer = RecordWriter.new(task, pageOutput)
        process(task, processorIndex, file_reader, record_writer)
        nil
      end
    end

    ## TODO
    #class LineParserPlugin < Java::BasicParserPlugin
    #end

    ## TODO
    #class TextParserPlugin < Java::BasicParserPlugin
    #end

  end
end
