<script lang=ts>
    export let document: any;
</script>
<div class="hidden lg:grid grid-flow-col justify-start items-center mt-2 gap-2">
    {#each document.TOC.slice(0,9) as elm, i}
        <a href="{document.url}#{elm}" class="hover:border-black border text-xs 
            text-stone-100 bg-neutral-500 rounded-lg p-1 truncate">{elm.replace(/_/g," ")}</a> 
        {#if i==8 && document.TOC.length > 10}
            <p class="text-xs text-black">...</p> 
        {/if}
    {/each}
</div>