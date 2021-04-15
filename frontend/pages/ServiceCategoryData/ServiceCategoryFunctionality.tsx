import { FC, useEffect, useState } from 'react'
import styled from 'styled-components'
import { useRouter } from 'next/router';
import Link from 'next/link';

import NavFrontendSpinner from 'nav-frontend-spinner';

import { fetchData } from 'utils/fetchServices';
import { retrieveFilteredServiceList } from 'utils/servicesOperations';
import { Ingress, Innholdstittel, Undertittel } from 'nav-frontend-typografi';
import { BackButton } from 'components/BackButton';
import { ErrorFilled, SuccessFilled, WarningFilled } from '@navikt/ds-icons';


const ErrorParagraph = styled.p`
    color: #ff4a4a;
    font-weight: bold;
    padding: 10px;
    border-radius: 5px;
`;
const SpinnerCentered = styled.div`
    position: absolute;
    top: 40%;
`

const CategoryContainer = styled.div`
    width: 100%;
    padding: 1rem 3rem;
    /* text-align: center; */
`
const ServiceContainer = styled.div`
    min-width: 100px;
    min-height: 75px;
    padding: 1rem 1rem;
    background-color: var(--navBakgrunn);
    border-radius: 10px;
    display: grid;
    justify-content: center;
    gap: 15px;
    grid-template-columns: repeat(1, 1fr);
`
const ServiceWrapper = styled.div`
    width: 100%;
    display: flex;
    justify-content: space-between;
`
const StatusIcon = styled.span`
    .up {
        color: var(--navGronn);
    }
    .down {
        color: var(--redError)
    }
    .issue {
        color: var(--navOransje)
    }
`
const CenterContent = styled.div`text-align: center;`

const formatStatusMessage = (serviceToFormat) =>   {
    switch (serviceToFormat.status) {
        case "OK":
            return (<SuccessFilled className="up" />)
        case "DOWN":
            return (<ErrorFilled className="down" />)
        case "ISSUE":
            return (<WarningFilled className="issue" />)
        default:
            break
    }
}

const ServiceCategoryFunctionality = () => {
    const [areas, setAreas] = useState([])
    const [isLoading, setIsLoading] = useState(false)
    
    const router = useRouter()
    const categoryId: string[] | string = router.query.id

    useEffect(() => {
        (async function () {
            setIsLoading(true)
            const newAreas = await fetchData()
            const parsedAreas = [...newAreas]
            setAreas(parsedAreas)
            setIsLoading(false)
        })()
    }, [])
    
    
    if (!areas) {
        return <ErrorParagraph>Kunne ikke hente de digitale tjenestene. Hvis problemet vedvarer, kontakt support.</ErrorParagraph>
    }

    let filteredArea = retrieveFilteredServiceList(areas, categoryId)
    if(filteredArea === undefined) {
        return <ErrorParagraph>Kategorien eksisterer ikke. Hvis du mener dette er feil, ta kontakt med kundeservice.</ErrorParagraph>
    }

    if (isLoading) {
        return (
            <SpinnerCentered>
                <NavFrontendSpinner type="XXL" />
            </SpinnerCentered>
        ) 
    }

    return (
        <CategoryContainer>
            <Link href="/"><span><BackButton /></span></Link>
            <CenterContent><Innholdstittel>{filteredArea.name}</Innholdstittel></CenterContent>
            <ServiceContainer>
                {filteredArea.services.map(service => {
                    return (
                        <ServiceWrapper key={service.name}>
                            <span><Undertittel>Tjenestenavn: <Ingress>{service.name}</Ingress></Undertittel></span>
                            <StatusIcon>{formatStatusMessage(service)}</StatusIcon>
                        </ServiceWrapper>
                    )
                })}
            </ServiceContainer>
        </CategoryContainer>
    )
}

export default ServiceCategoryFunctionality