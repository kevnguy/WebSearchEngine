<script lang=ts>
    import ImageCard from './imageCard.svelte';
import TocCard from './tocCard.svelte';
    
    export let document: any;
    let hovering = false;
    const enter = () => (hovering = true);
    const leave = () => (hovering = false);
    let date = new Date(document.lastMod).toLocaleDateString(undefined, {  
        day:   'numeric',
        month: 'short',
        year:  'numeric',
    });
</script>
<div class="py-2.5 grid gap-3 w-11/12">
    <div class="flex flex-col relative min-w-0">
        <div class="p-5 bg-gray-200 text-grey-800 rounded-md shadow-sm hover:shadow-lg">
            <a href={document.url} on:mouseleave={leave} class="">
                <div class="flex flex-row min-w-0 truncate">
                    <p class="text-sm text-neutral-500 truncate pr-1">{document.url}</p>
                    <p class="text-sm text-neutral-500 pr-1">-</p>
                    <p class="text-sm text-neutral-500 pr-5 truncate">{date}</p>
                </div>
                <p class="hover:underline text-blue-500 font-medium">{document.title}</p>   
                {#if document.img}
                    <svg on:mouseenter={enter} class= "absolute top-0 right-0 w-4 h-4 mx-4 my-6" >
                        <g fill="currentColor">
                            <path d="M6.002 5.5a1.5 1.5 0 1 1-3 0a1.5 1.5 0 0 1 3 0z"></path>
                            <path d="M2.002 1a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V3a2 2 0 0 0-2-2h-12zm12 
                                1a1 1 0 0 1 1 1v6.5l-3.777-1.947a.5.5 0 0 0-.577.093l-3.71 3.71l-2.66-1.772a.5.5 0 0 
                                0-.63.062L1.002 12V3a1 1 0 0 1 1-1h12z">
                            </path>
                        </g>
                    </svg>
                    {#if hovering}
                        <ImageCard document={document}></ImageCard>
                    {/if }
                {/if}     
            </a>
            <p>{@html document.snippet}</p>
            {#if document.TOC}
                <TocCard document={document}></TocCard>
            {/if}
        </div>
    </div>
</div>