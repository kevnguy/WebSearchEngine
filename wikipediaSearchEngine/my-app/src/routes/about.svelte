<script lang=ts>
    console.log("Not so secret page")
    let regular: string = "https://lucene.apache.org/core/9_2_0/queryparser/org/apache/lucene/queryparser/flexible/standard/StandardQueryParser.html"
    let multifield: string = "https://lucene.apache.org/core/9_2_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html"
</script>
<svelte:head>
    <title>Nice group</title>
</svelte:head>
<h1 class="text-4xl text-center my-8">About the Project</h1>

<div class="flex flex-col justify-center items-center bg-neutral-200 rounded-lg p-3">
    <p>Search engine for wikipedia pages!</p>
    <p>If there is an error, an error message will appear below the search bar. More details can be found on the backend logs.</p>
    <div class="p-3 bg-emerald-100 max-w-3xl rounded-2xl">
        <p class="text-center font-medium">Usage</p>
        <p>Enter your query in the search bar and press the "SEARCH" button or return key. 
            The top 10 results will be displayed with the url, date, title of page, snippits, and image and
            and table of contents if available.
        </p>
        <p class="text-center mt-5 font-medium">Features of the document cards:</p>
        <p> The url, date, title of the page are clickable and will take you to the page. 
            Hover over the image icon to expose an image found on the page. 
            Clicking on the image will also take you to the page. 
            The first 10 items in the TOC are listed below the snippits; 
            clicking on those will take you to the corresponding section on that page.
        </p>
        <p class="text-center mt-5 font-medium">Advanced Search Options:</p>
        <ul>
            <li>Searchable fields include: mainText (default), italicText, boldText, title, and lastMod</li>
            <li>When using the BM25 option the search engine support a variety of options.
                Please visit the offical <a class="underline text-sky-700" href={regular}>documentation</a> to learn supported operations and syntax. 
                Range queries are on the lastMod field follow a YYYYMMDD pattern.
            </li>
            <li>The BM25 multifield option provides more relevent results at the cost of less flexability.
                Please visit the offical <a class="underline text-sky-700" href={multifield}>documentation</a> to learn supported operations and syntax. 
                Limited support for range queries due to Lucene's maxClauseCount.
            </li>
        </ul>    
    </div> 
    <div class="p-3 bg-red-200 max-w-xl rounded-2xl">
        <p class="text-center font-medium">Known issues:</p>
        <ul>
            <li>May encounter errors with fuzzy search because Lucene highlighter is used to generate snippets.
                It is unknown what is causing it to throw errors but a user online suggest
                it has to do with 
                <a class="underline text-sky-700"href="https://issues.apache.org/jira/browse/LUCENE-9568">apostraphes</a>.
                There is no known workaround at this time.
            </li>
        </ul>
    </div> 
</div>