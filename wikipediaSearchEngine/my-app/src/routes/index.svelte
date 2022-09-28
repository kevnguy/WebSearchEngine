<script lang="ts">
    import {documents, searchDocs} from "../stores/wikistore";
    import DocCard from "../components/docCard.svelte";
    import { onMount } from "svelte";

    let instantSearch:boolean = false;
    let query: string = "";
    let Multifield:boolean = false;
    $: {
        if(instantSearch && query.trim())
            searchDocs(query,Multifield);
    }

</script>
<svelte:head>
    <title>GG go next</title>
</svelte:head>
<svelte:body class="bg-slate-500"></svelte:body>
<h1 class="text-3xl text-center my-4 font-semibold">Wikipedia Search Engine</h1>

<div class="flex justify-center">
    <form class="input-group flex w-1/2 relative rounded-md text-xs p-1 border-2 border-grey-200" on:submit|preventDefault>
        <input class="form-control w-full relative px-2 placeholder:italic" type="search" placeholder="Search Documents..." bind:value={query}>
        <button class="btn px-3 py-1.5 border-2 hover:bg-indigo-400" on:click={() => {
                if(query.trim())
                    searchDocs(query, Multifield);
            }}>SEARCH</button>
    </form>
    <label for="toggleModel" class="hidden md:flex p-3 items-center">
        <p class="mr-3 text-gray-700 font-medium">BM25</p>
        <div class="relative">
            <input tabindex="0" type="checkbox" id="toggleModel" class="sr-only" on:click="{() => Multifield = !Multifield }">
            <div class="block bg-gray-600 w-8 h-5 rounded-full"></div>
            <div class="dot absolute left-1 top-1 bg-white w-3 h-3 rounded-full transition"></div>
        </div>
        <p class="ml-3 text-gray-700 font-medium">Multifield BM25</p>
</div>
<span id="error" class="flex justify-center"></span>

{#each $documents as doc}
    {#if doc}
        <div class="flex justify-center">
            <DocCard document={doc}></DocCard>
        </div>
    {/if}
{:else}
    <div id="results" class="flex items-center flex-col py-10">
        <div class="bg-white p-2 rounded-md">
            <p class="text-center z-10">No Documents found :(</p>
            <img class="w-52" src="https://ae01.alicdn.com/kf/HTB1EHoTXQOWBuNjSsppq6xPgpXaf/Gintama-sadaharu-Bite-head-anime-Costumes-Hats.jpg" alt="No content">
        </div>
    </div> 
{/each}

<div class="absolute top-0 right-0 p-2">
    <div class="flex flex-col items-center form-check">
        <label class="form-check-label text-red-900 text-xs" for="switch">Instant Search</label>
        <input on:click="{() => instantSearch = !instantSearch}" class="invisible hover:visible h-3 w-3 rounded-full" type="checkbox" role="switch" id="switch">
    </div> 
</div>